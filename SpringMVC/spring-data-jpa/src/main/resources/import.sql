insert into book (id, title, authors, status) values (1, 'First book', 'Jan Kowalski', 'FREE');
insert into book (id, title, authors, status) values (2, 'Second book', 'Zbigniew Nowak', 'FREE');
insert into book (id, title, authors, status) values (3, 'Third book', 'Janusz Jankowski', 'FREE');
insert into book (id, title, authors, status) values (4, 'Fourth book', 'Janusz Jankowski', 'MISSING');
insert into book (id, title, authors, status) values (5, 'Fifth book', 'Iwan Iwanowicz', 'LOAN');
insert into book (id, title, authors, status) values (6, 'Sixth book', 'Iwan Iwanowicz', 'FREE');

insert into userentity (id, user_name, password, user_role) values (1, 'admin', 'admin', 'ROLE_ADMIN');
insert into userentity (id, user_name, password, user_role) values (2, 'user', 'user', 'ROLE_USER');
insert into userentity (id, user_name, password, user_role) values (3, 'super', 'super', 'ROLE_SUPER');