-- Allow descriptive (Theory) questions that have no MCQ options
ALTER TABLE questions
    ALTER COLUMN option_a       DROP NOT NULL,
    ALTER COLUMN option_b       DROP NOT NULL,
    ALTER COLUMN correct_answer DROP NOT NULL;

-- Drop old CHAR(1) check, replace with one that permits NULL
ALTER TABLE questions DROP CONSTRAINT IF EXISTS questions_correct_answer_check;
ALTER TABLE questions ADD CONSTRAINT questions_correct_answer_check
    CHECK (correct_answer IS NULL OR correct_answer IN ('A', 'B', 'C', 'D'));

-- question_type: MCQ (default) or DESCRIPTIVE
ALTER TABLE questions ADD COLUMN IF NOT EXISTS question_type VARCHAR(15) NOT NULL DEFAULT 'MCQ';

-- descriptive_answer stores the long-form answer for DESCRIPTIVE questions
ALTER TABLE questions ADD COLUMN IF NOT EXISTS descriptive_answer TEXT;
