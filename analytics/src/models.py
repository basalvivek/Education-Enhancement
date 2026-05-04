from sqlalchemy import Column, BigInteger, String, Boolean, Integer, Float, Text, DateTime, ForeignKey
from sqlalchemy.orm import declarative_base, relationship

Base = declarative_base()


class User(Base):
    __tablename__ = "users"
    id = Column(BigInteger, primary_key=True)
    name = Column(String)
    email = Column(String)
    role = Column(String)
    exams = relationship("Exam", back_populates="student")


class Category(Base):
    __tablename__ = "categories"
    id = Column(BigInteger, primary_key=True)
    name = Column(String)
    classes = relationship("Class", back_populates="category")


class Class(Base):
    __tablename__ = "classes"
    id = Column(BigInteger, primary_key=True)
    name = Column(String)
    category_id = Column(BigInteger, ForeignKey("categories.id"))
    category = relationship("Category", back_populates="classes")
    topics = relationship("Topic", back_populates="a_class")


class Topic(Base):
    __tablename__ = "topics"
    id = Column(BigInteger, primary_key=True)
    name = Column(String)
    class_id = Column(BigInteger, ForeignKey("classes.id"))
    a_class = relationship("Class", back_populates="topics")
    contents = relationship("Content", back_populates="topic")


class Content(Base):
    __tablename__ = "content"
    id = Column(BigInteger, primary_key=True)
    title = Column(String)
    content_type = Column(String)
    topic_id = Column(BigInteger, ForeignKey("topics.id"))
    topic = relationship("Topic", back_populates="contents")
    exams = relationship("Exam", back_populates="content")


class Exam(Base):
    __tablename__ = "exams"
    id = Column(BigInteger, primary_key=True)
    student_id = Column(BigInteger, ForeignKey("users.id"))
    content_id = Column(BigInteger, ForeignKey("content.id"))
    score = Column(Integer)
    total = Column(Integer)
    started_at = Column(DateTime)
    submitted_at = Column(DateTime)
    student = relationship("User", back_populates="exams")
    content = relationship("Content", back_populates="exams")
    answers = relationship("ExamAnswer", back_populates="exam")


class Question(Base):
    __tablename__ = "questions"
    id = Column(BigInteger, primary_key=True)
    question_text = Column(Text)
    correct_answer = Column(String)
    topic_id = Column(BigInteger, ForeignKey("topics.id"))
    answers = relationship("ExamAnswer", back_populates="question")


class ExamAnswer(Base):
    __tablename__ = "exam_answers"
    id = Column(BigInteger, primary_key=True)
    exam_id = Column(BigInteger, ForeignKey("exams.id"))
    question_id = Column(BigInteger, ForeignKey("questions.id"))
    student_answer = Column(String)
    is_correct = Column(Boolean)
    exam = relationship("Exam", back_populates="answers")
    question = relationship("Question", back_populates="answers")
