
create table booking (
                         booking_price integer not null,
                         booking_id bigint AUTO_INCREMENT,
                         created_at timestamp(6),
                         expired_at timestamp(6),
                         seat_id bigint,
                         updated_at timestamp(6),
                         user_id bigint,
                         status enum ('CANCELLED','PAID','RESERVED'),
                         primary key (booking_id)
);

create table concert (
                         concert_id bigint AUTO_INCREMENT,
                         artist varchar(255),
                         place varchar(255),
                         title varchar(255),
                         primary key (concert_id)
);

create table concert_schedule (
                                  capacity integer not null,
                                  concert_date date,
                                  occupied_count integer not null,
                                  concert_id bigint,
                                  concert_schedule_id bigint AUTO_INCREMENT,
                                  primary key (concert_schedule_id)
);

create table payment (
                         amount bigint not null,
                         booking_id bigint unique,
                         paid_at timestamp(6),
                         payment_id bigint AUTO_INCREMENT,
                         user_id bigint,
                         primary key (payment_id)
);

create table seat (
                      is_occupied boolean not null,
                      price integer not null,
                      seat_no integer not null,
                      concert_schedule_id bigint,
                      seat_id bigint AUTO_INCREMENT,
                      row_name varchar(255),
                      primary key (seat_id)
);

create table token_queue (
                             access_capacity integer not null,
                             concert_id bigint,
                             token_queue_id bigint AUTO_INCREMENT,
                             primary key (token_queue_id)
);

create table token_queue_item (
                                  position integer not null,
                                  created_at timestamp(6),
                                  expired_at timestamp(6),
                                  id bigint AUTO_INCREMENT,
                                  token_queue_id bigint,
                                  user_id bigint,
                                  status enum ('PASSED','WAITING'),
                                  primary key (id)
);

create table user_point (
                            point bigint not null,
                            updated_time timestamp(6),
                            user_id bigint unique,
                            user_point_id bigint AUTO_INCREMENT,
                            primary key (user_point_id)
);

create table users (
                       user_id bigint AUTO_INCREMENT,
                       name varchar(255),
                       uuid varchar(255) unique,
                       primary key (user_id)
);

alter table if exists booking
    add constraint FK7ryitbom1ln9okwlj2t9tt9ym
        foreign key (seat_id)
            references seat;

alter table if exists booking
    add constraint FK7udbel7q86k041591kj6lfmvw
        foreign key (user_id)
            references users;

alter table if exists concert_schedule
    add constraint FK3ry7aiaia6ooa3ajwf6w6soci
        foreign key (concert_id)
            references concert;

alter table if exists payment
    add constraint FKqewrl4xrv9eiad6eab3aoja65
        foreign key (booking_id)
            references booking;

alter table if exists payment
    add constraint FKmi2669nkjesvp7cd257fptl6f
        foreign key (user_id)
            references users;

alter table if exists seat
    add constraint FKjtsoupodivwr6xa4vk02e2qol
        foreign key (concert_schedule_id)
            references concert_schedule;

alter table if exists token_queue
    add constraint FKi8pc31j8p8ol040m3vtmbsk8s
        foreign key (concert_id)
            references concert;

alter table if exists token_queue_item
    add constraint FKn6ep1c2sbhvf7ljwlpadftckc
        foreign key (token_queue_id)
            references token_queue;

alter table if exists token_queue_item
    add constraint FKe2h087wfp7vgrro3c3n3i4qut
        foreign key (user_id)
            references users;

alter table if exists user_point
    add constraint FKl7fer3vdwhdrg5tj3rhwf130n
        foreign key (user_id)
            references users;