create table posts_files(
    id serial PRIMARY KEY,
    post_id int not null references posts(id),
    file_id int not null references files(id)
);