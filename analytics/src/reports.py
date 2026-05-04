from sqlalchemy.orm import Session
from .models import User, Exam
from .weak_area import get_weak_areas
from .trends import get_trends
from typing import Dict, Any
from datetime import datetime


def generate_report(db: Session, student_id: int) -> Dict[str, Any]:
    student = db.query(User).filter(User.id == student_id).first()
    if not student:
        return {"error": f"Student {student_id} not found"}

    completed = (
        db.query(Exam)
        .filter(Exam.student_id == student_id, Exam.submitted_at.isnot(None))
        .order_by(Exam.submitted_at.desc())
        .all()
    )

    weak_data = get_weak_areas(db, student_id)
    trend_data = get_trends(db, student_id)

    overall_avg = 0.0
    if completed:
        overall_avg = round(
            sum((e.score / e.total * 100) for e in completed if e.total) / len(completed), 1
        )

    recent_exams = [
        {
            "exam_id": e.id,
            "topic": e.content.topic.name,
            "content": e.content.title,
            "score": e.score,
            "total": e.total,
            "percentage": round(e.score / e.total * 100, 1) if e.total else 0,
            "date": e.submitted_at.strftime("%Y-%m-%d %H:%M"),
        }
        for e in completed[:10]
    ]

    recommendations = _build_recommendations(weak_data, trend_data, overall_avg)

    return {
        "generated_at": datetime.now().strftime("%Y-%m-%d %H:%M"),
        "student": {"id": student.id, "name": student.name, "email": student.email},
        "summary": {
            "total_exams": len(completed),
            "overall_average": overall_avg,
            "weak_topics_count": len(weak_data["weak_areas"]),
            "strong_topics_count": len(weak_data["strong_areas"]),
            "overall_trend": trend_data.get("overall_trend", "no_data"),
        },
        "weak_areas": weak_data["weak_areas"],
        "strong_areas": weak_data["strong_areas"],
        "recent_exams": recent_exams,
        "recommendations": recommendations,
    }


def _build_recommendations(weak_data: dict, trend_data: dict, overall_avg: float) -> list:
    recs = []

    for topic in weak_data["weak_areas"]:
        recs.append({
            "priority": "HIGH",
            "message": f"Revise '{topic['topic_name']}' — average {topic['average_percentage']}%. "
                       f"Focus on this before moving on.",
        })

    for t in trend_data.get("topics", []):
        if t["trend"] == "declining":
            recs.append({
                "priority": "MEDIUM",
                "message": f"'{t['topic_name']}' scores are declining. Review recent mistakes.",
            })
        elif t["trend"] == "improving":
            recs.append({
                "priority": "LOW",
                "message": f"Great progress on '{t['topic_name']}' — keep it up!",
            })

    if overall_avg >= 80:
        recs.append({"priority": "LOW", "message": "Excellent overall performance! Try Mock Exams for a challenge."})
    elif overall_avg < 50:
        recs.append({"priority": "HIGH", "message": "Overall score is below 50%. Consider revisiting theory content."})

    return recs
