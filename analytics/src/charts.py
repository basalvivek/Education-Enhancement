import io
import base64
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
import seaborn as sns
from sqlalchemy.orm import Session
from .models import Exam
from typing import Optional

sns.set_theme(style="whitegrid")
COLORS = {"weak": "#e74c3c", "strong": "#2ecc71", "neutral": "#3498db", "bg": "#f8f9fa"}


def _fig_to_base64(fig) -> str:
    buf = io.BytesIO()
    fig.savefig(buf, format="png", bbox_inches="tight", dpi=120)
    buf.seek(0)
    encoded = base64.b64encode(buf.read()).decode("utf-8")
    plt.close(fig)
    return encoded


def topic_progress_chart(db: Session, student_id: int) -> Optional[str]:
    exams = (
        db.query(Exam)
        .filter(Exam.student_id == student_id, Exam.submitted_at.isnot(None))
        .order_by(Exam.submitted_at)
        .all()
    )
    if not exams:
        return None

    topic_avgs: dict = {}
    for e in exams:
        name = e.content.topic.name
        pct = (e.score / e.total * 100) if e.total else 0
        topic_avgs.setdefault(name, []).append(pct)

    topics = list(topic_avgs.keys())
    avgs = [round(sum(v) / len(v), 1) for v in topic_avgs.values()]
    colors = [COLORS["weak"] if a < 60 else COLORS["strong"] for a in avgs]

    fig, ax = plt.subplots(figsize=(max(8, len(topics) * 1.4), 5))
    bars = ax.bar(topics, avgs, color=colors, edgecolor="white", linewidth=1.5)
    ax.axhline(60, color="#e74c3c", linestyle="--", linewidth=1.2, label="Weak threshold (60%)")
    ax.set_ylim(0, 110)
    ax.set_ylabel("Average Score (%)", fontsize=11)
    ax.set_title("Topic Performance Overview", fontsize=14, fontweight="bold", pad=15)

    for bar, val in zip(bars, avgs):
        ax.text(bar.get_x() + bar.get_width() / 2, bar.get_height() + 2,
                f"{val}%", ha="center", va="bottom", fontsize=10, fontweight="bold")

    weak_patch = mpatches.Patch(color=COLORS["weak"], label="Below 60% (Weak)")
    strong_patch = mpatches.Patch(color=COLORS["strong"], label="60%+ (Passing)")
    ax.legend(handles=[weak_patch, strong_patch, ax.lines[0]], loc="upper right")
    plt.xticks(rotation=20, ha="right")
    plt.tight_layout()
    return _fig_to_base64(fig)


def score_trend_chart(db: Session, student_id: int, topic_id: Optional[int] = None) -> Optional[str]:
    query = db.query(Exam).filter(
        Exam.student_id == student_id, Exam.submitted_at.isnot(None)
    )
    if topic_id:
        query = query.filter(Exam.content.has(topic_id=topic_id))
    exams = query.order_by(Exam.submitted_at).all()

    if len(exams) < 2:
        return None

    dates = [e.submitted_at.strftime("%m/%d %H:%M") for e in exams]
    scores = [round((e.score / e.total * 100), 1) if e.total else 0 for e in exams]
    labels = [e.content.topic.name for e in exams]

    fig, ax = plt.subplots(figsize=(max(8, len(exams) * 1.2), 5))
    ax.plot(dates, scores, marker="o", linewidth=2, markersize=8,
            color=COLORS["neutral"], markerfacecolor="white", markeredgewidth=2)
    ax.fill_between(dates, scores, alpha=0.1, color=COLORS["neutral"])
    ax.axhline(60, color="#e74c3c", linestyle="--", linewidth=1.2, label="Weak threshold (60%)")

    for i, (d, s, l) in enumerate(zip(dates, scores, labels)):
        ax.annotate(f"{s}%\n({l})", (d, s), textcoords="offset points",
                    xytext=(0, 12), ha="center", fontsize=8)

    ax.set_ylim(0, 115)
    ax.set_ylabel("Score (%)", fontsize=11)
    ax.set_title("Score Trend Over Time", fontsize=14, fontweight="bold", pad=15)
    ax.legend()
    plt.xticks(rotation=30, ha="right")
    plt.tight_layout()
    return _fig_to_base64(fig)


def weak_area_pie_chart(db: Session, student_id: int) -> Optional[str]:
    exams = (
        db.query(Exam)
        .filter(Exam.student_id == student_id, Exam.submitted_at.isnot(None))
        .all()
    )
    if not exams:
        return None

    topic_avgs: dict = {}
    for e in exams:
        name = e.content.topic.name
        pct = (e.score / e.total * 100) if e.total else 0
        topic_avgs.setdefault(name, []).append(pct)

    weak_count = sum(1 for v in topic_avgs.values() if sum(v) / len(v) < 60)
    strong_count = len(topic_avgs) - weak_count

    if weak_count == 0 and strong_count == 0:
        return None

    fig, ax = plt.subplots(figsize=(6, 6))
    sizes = [weak_count, strong_count]
    labels = [f"Needs Improvement\n({weak_count})", f"Passing\n({strong_count})"]
    colors = [COLORS["weak"], COLORS["strong"]]
    explode = (0.05, 0)

    wedges, texts, autotexts = ax.pie(
        sizes, labels=labels, colors=colors, explode=explode,
        autopct="%1.0f%%", startangle=90,
        textprops={"fontsize": 11},
        wedgeprops={"edgecolor": "white", "linewidth": 2}
    )
    for at in autotexts:
        at.set_fontweight("bold")
        at.set_fontsize(12)

    ax.set_title("Topic Performance Distribution", fontsize=14, fontweight="bold", pad=15)
    plt.tight_layout()
    return _fig_to_base64(fig)
