from fastapi import FastAPI, Depends, HTTPException
from fastapi.responses import JSONResponse
from sqlalchemy.orm import Session
from .database import get_db
from .weak_area import get_weak_areas
from .trends import get_trends
from .reports import generate_report
from .charts import topic_progress_chart, score_trend_chart, weak_area_pie_chart
from typing import Optional

app = FastAPI(
    title="Education Analytics API",
    description="Analytics service for the Education Enhancement Portal",
    version="1.0.0",
)


@app.get("/health")
def health():
    return {"status": "ok"}


@app.get("/analytics/student/{student_id}/weak-areas")
def weak_areas(student_id: int, db: Session = Depends(get_db)):
    return get_weak_areas(db, student_id)


@app.get("/analytics/student/{student_id}/trends")
def trends(student_id: int, db: Session = Depends(get_db)):
    return get_trends(db, student_id)


@app.get("/analytics/student/{student_id}/report")
def report(student_id: int, db: Session = Depends(get_db)):
    result = generate_report(db, student_id)
    if "error" in result:
        raise HTTPException(status_code=404, detail=result["error"])
    return result


@app.get("/analytics/student/{student_id}/charts/topics")
def chart_topics(student_id: int, db: Session = Depends(get_db)):
    img = topic_progress_chart(db, student_id)
    if not img:
        raise HTTPException(status_code=404, detail="No exam data for this student.")
    return {"chart": img, "type": "topic_progress", "format": "base64_png"}


@app.get("/analytics/student/{student_id}/charts/trend")
def chart_trend(student_id: int, topic_id: Optional[int] = None, db: Session = Depends(get_db)):
    img = score_trend_chart(db, student_id, topic_id)
    if not img:
        raise HTTPException(status_code=404, detail="Need at least 2 exams to show a trend.")
    return {"chart": img, "type": "score_trend", "format": "base64_png"}


@app.get("/analytics/student/{student_id}/charts/pie")
def chart_pie(student_id: int, db: Session = Depends(get_db)):
    img = weak_area_pie_chart(db, student_id)
    if not img:
        raise HTTPException(status_code=404, detail="No exam data for this student.")
    return {"chart": img, "type": "weak_area_distribution", "format": "base64_png"}
