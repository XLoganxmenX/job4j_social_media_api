create table messages(
    id serial PRIMARY KEY,
    from_user_id int not null references users(id),
    to_user_id int not null references users(id),
    message text not null,
    created timestamp not null
)