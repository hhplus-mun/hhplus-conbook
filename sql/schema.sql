create table booking
(
    booking_price integer not null,
    booking_id    bigint  not null auto_increment,
    created_at    datetime(6),
    expired_at    datetime(6),
    seat_id       bigint,
    updated_at    datetime(6),
    user_id       bigint,
    status        enum ('CANCELLED','PAID','RESERVED'),
    primary key (booking_id)
) engine=InnoDB;

create table concert
(
    concert_id bigint not null auto_increment,
    artist     varchar(255),
    place      varchar(255),
    title      varchar(255),
    primary key (concert_id)
) engine=InnoDB;

create table concert_schedule
(
    capacity            integer not null,
    concert_date        date,
    occupied_count      integer not null,
    concert_id          bigint,
    concert_schedule_id bigint  not null auto_increment,
    primary key (concert_schedule_id)
) engine=InnoDB;

create table payment
(
    amount     bigint not null,
    booking_id bigint,
    paid_at    datetime(6),
    payment_id bigint not null auto_increment,
    user_id    bigint,
    primary key (payment_id)
) engine=InnoDB;

create table seat
(
    is_occupied         bit     not null,
    price               integer not null,
    seat_no             integer not null,
    concert_schedule_id bigint,
    seat_id             bigint  not null auto_increment,
    row_name            varchar(255),
    primary key (seat_id)
) engine=InnoDB;

create table token_history
(
    concert_id       bigint,
    expires_at       datetime(6),
    issued_at        datetime(6),
    token_history_id bigint       not null auto_increment,
    user_uuid        varchar(255) not null,
    token_type       enum ('ACCESS','WAIT'),
    primary key (token_history_id)
) engine=InnoDB;

create table user_point
(
    point         bigint not null,
    updated_time  datetime(6),
    user_id       bigint,
    user_point_id bigint not null auto_increment,
    primary key (user_point_id)
) engine=InnoDB;

create table users
(
    user_id bigint not null auto_increment,
    name    varchar(255),
    uuid    varchar(255),
    primary key (user_id)
) engine=InnoDB;

create table token
(
    concert_id  bigint   not null,
    created_at  datetime not null,
    expired_at  datetime,
    id          bigint AUTO_INCREMENT,
    user_uuid   varchar(255),
    status      enum ('PASSED','WAITING'),
    token_value varchar(255),
    primary key (id)
);

alter table if exists payment
    add constraint UKku02qy6369hn9uhy3n7jk9v6e unique (booking_id);

alter table if exists user_point
    add constraint UKc30japbjwomtd7t6mb3lgxve6 unique (user_id);

alter table if exists users
    add constraint UK6km2m9i3vjuy36rnvkgj1l61s unique (uuid);

alter table if exists booking
    add constraint FK7ryitbom1ln9okwlj2t9tt9ym
        foreign key (seat_id)
            references seat (seat_id);

alter table if exists booking
    add constraint FK7udbel7q86k041591kj6lfmvw
        foreign key (user_id)
            references users (user_id);

alter table if exists concert_schedule
    add constraint FK3ry7aiaia6ooa3ajwf6w6soci
        foreign key (concert_id)
            references concert (concert_id);

alter table if exists payment
    add constraint FKqewrl4xrv9eiad6eab3aoja65
        foreign key (booking_id)
            references booking (booking_id);

alter table if exists payment
    add constraint FKmi2669nkjesvp7cd257fptl6f
        foreign key (user_id)
            references users (user_id);

alter table if exists seat
    add constraint FKjtsoupodivwr6xa4vk02e2qol
        foreign key (concert_schedule_id)
            references concert_schedule (concert_schedule_id);

alter table if exists token_history
    add constraint FKrnrj0wvofy76c815yq3tbqcg8
        foreign key (concert_id)
            references concert (concert_id);

alter table if exists user_point
    add constraint FKl7fer3vdwhdrg5tj3rhwf130n
        foreign key (user_id)
            references users (user_id);