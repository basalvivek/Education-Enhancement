-- Hibernate validates CHAR(1) as bpchar which conflicts with VARCHAR mapping
ALTER TABLE questions    ALTER COLUMN correct_answer TYPE VARCHAR(1);
ALTER TABLE exam_answers ALTER COLUMN student_answer TYPE VARCHAR(1);
