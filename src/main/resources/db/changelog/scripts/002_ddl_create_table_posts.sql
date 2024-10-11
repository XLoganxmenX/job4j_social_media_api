create table posts
(
    id serial primary key,
    title text not null,
    description text not null,
    created timestamp not null,
    user_id int references users (id) not null
);