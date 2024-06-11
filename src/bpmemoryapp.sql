create database MG;
use MG;

create table guest(
                      guest_id       int         primary key auto_increment
    ,   first_name     varchar(40) not null
    ,   last_name      varchar(40) not null
    ,   guest_code     varchar(25) not null
    ,   age            int not null
    ,   geboorte_datum date
);

create table game(
                     game_id          int primary key auto_increment
    ,   guest_scores     int not null
    ,   guest_id         int not null
    ,   played_games     int not null
    ,   foreign key (guest_id) references guest(guest_id)
);

alter table game drop first_name
;
alter table game
    add foreign key (last_name) references guest(last_name ) ;

select first_name
     ,      last_name
     ,      guest_scores
     ,      played_games
from guest gst
         join game game on game.guest_id = gst.guest_id
;

select *
from guest
;

select *
from game
;
