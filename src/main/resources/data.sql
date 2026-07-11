DELETE FROM ratings;
DELETE FROM loans;
DELETE FROM members;
DELETE FROM books;

INSERT INTO books (id, isbn, title, author, total_copies, available_copies) VALUES
    (1, '9780143127741', 'Dune', 'Frank Herbert', 3, 2);

INSERT INTO books (id, isbn, title, author, total_copies, available_copies) VALUES
    (2, '9780132350884', 'Clean Code', 'Robert C. Martin', 2, 2);

INSERT INTO books (id, isbn, title, author, total_copies, available_copies) VALUES
    (3, '9780201633610', 'Design Patterns', 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides', 1, 1);

INSERT INTO members (id, full_name, email) VALUES
    (1, 'Alice Jensen', 'alice@example.com');

INSERT INTO members (id, full_name, email) VALUES
    (2, 'Kim Larsen', 'kim@example.com');

INSERT INTO loans (id, book_id, member_id, checked_out_at, returned_at, status) VALUES
    (1, 1, 1, TIMESTAMP '2026-07-09 10:00:00', NULL, 'ACTIVE');

INSERT INTO loans (id, book_id, member_id, checked_out_at, returned_at, status) VALUES
    (2, 2, 2, TIMESTAMP '2026-07-01 09:30:00', TIMESTAMP '2026-07-06 16:15:00', 'RETURNED');

INSERT INTO ratings (id, book_id, member_id, score, feedback, rated_at) VALUES
    (1, 1, 1, 5, 'Excellent sci-fi world building.', TIMESTAMP '2026-07-09 18:30:00');

INSERT INTO ratings (id, book_id, member_id, score, feedback, rated_at) VALUES
    (2, 1, 2, 4, 'Strong story, a little dense at times.', TIMESTAMP '2026-07-09 19:15:00');

INSERT INTO ratings (id, book_id, member_id, score, feedback, rated_at) VALUES
    (3, 2, 1, 5, 'Clear and practical.', TIMESTAMP '2026-07-07 12:00:00');

ALTER TABLE books ALTER COLUMN id RESTART WITH 4;
ALTER TABLE members ALTER COLUMN id RESTART WITH 3;
ALTER TABLE loans ALTER COLUMN id RESTART WITH 3;
ALTER TABLE ratings ALTER COLUMN id RESTART WITH 4;