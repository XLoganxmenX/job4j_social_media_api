create table users_friendships(
    id serial PRIMARY KEY,
    user_id int not null references users(id),
    friend_user_id int not null references users(id),
    UNIQUE (user_id, friend_user_id)
)