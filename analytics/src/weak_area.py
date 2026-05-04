from sqlalchemy.orm import Session
from sqlalchemy import func
from .models import Exam, Content, Topic, Class, Category
from typing import List, Dict, Any

WEAK_THRESHOLD = 60.0


def get_weak_areas(db: Session, student_id: int) -> Dict[str, Any]:
    exams = (
        db.query(Exam)
        .filter(Exam.student_id == student_id, Exam.submitted_at.isnot(None))
        .all()
    )

    if not exams:
        return {"student_id": student_id, "weak_areas": [], "strong_areas": [], "summary": "No exams completed yet."}

    topic_stats: Dict[int, Dict] = {}

    for exam in exams:
        if not exam.total or exam.total == 0:
            continue
        topic = exam.content.topic
        pct = (exam.score / exam.total) * 100

        if topic.id not in topic_stats:
            topic_stats[topic.id] = {
                "topic_id": topic.id,
                "topic_name": topic.name,
                "class_name": topic.a_class.name,
                "category_name": topic.a_class.category.name,
                "scores": [],
                "attempts": 0,
            }
        topic_stats[topic.id]["scores"].append(pct)
        topic_stats[topic.id]["attempts"] += 1

    weak, strong = [], []

    for stats in topic_stats.values():
        avg = sum(stats["scores"]) / len(stats["scores"])
        entry = {
            "topic_id": stats["topic_id"],
            "topic_name": stats["topic_name"],
            "class_name": stats["class_name"],
            "category_name": stats["category_name"],
            "average_percentage": round(avg, 1),
            "attempts": stats["attempts"],
        }
        if avg < WEAK_THRESHOLD:
            weak.append(entry)
        else:
            strong.append(entry)

    weak.sort(key=lambda x: x["average_percentage"])
    strong.sort(key=lambda x: x["average_percentage"], reverse=True)

    return {
        "student_id": student_id,
        "weak_areas": weak,
        "strong_areas": strong,
        "summary": f"{len(weak)} weak topic(s) identified (below {WEAK_THRESHOLD}%).",
    }
