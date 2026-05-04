from sqlalchemy.orm import Session
from .models import Exam, Topic
from typing import Dict, Any, List


def get_trends(db: Session, student_id: int) -> Dict[str, Any]:
    exams = (
        db.query(Exam)
        .filter(Exam.student_id == student_id, Exam.submitted_at.isnot(None))
        .order_by(Exam.submitted_at)
        .all()
    )

    if not exams:
        return {"student_id": student_id, "topics": [], "overall_trend": "no_data"}

    topic_timelines: Dict[int, List] = {}

    for exam in exams:
        topic = exam.content.topic
        if topic.id not in topic_timelines:
            topic_timelines[topic.id] = {
                "topic_id": topic.id,
                "topic_name": topic.name,
                "class_name": topic.a_class.name,
                "timeline": [],
            }
        pct = round((exam.score / exam.total) * 100, 1) if exam.total else 0
        topic_timelines[topic.id]["timeline"].append({
            "exam_id": exam.id,
            "date": exam.submitted_at.strftime("%Y-%m-%d %H:%M"),
            "score": exam.score,
            "total": exam.total,
            "percentage": pct,
        })

    topics = []
    for data in topic_timelines.values():
        timeline = data["timeline"]
        trend = _calculate_trend([t["percentage"] for t in timeline])
        topics.append({**data, "trend": trend, "latest_percentage": timeline[-1]["percentage"]})

    overall_scores = [e.score / e.total * 100 for e in exams if e.total]
    overall_trend = _calculate_trend(overall_scores)

    return {"student_id": student_id, "topics": topics, "overall_trend": overall_trend}


def _calculate_trend(scores: List[float]) -> str:
    if len(scores) < 2:
        return "insufficient_data"
    delta = scores[-1] - scores[0]
    if delta > 5:
        return "improving"
    elif delta < -5:
        return "declining"
    return "stable"
