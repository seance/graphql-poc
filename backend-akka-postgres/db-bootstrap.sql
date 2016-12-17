-- Schema

CREATE TABLE weapons (
  id    INTEGER PRIMARY KEY,
  name  VARCHAR NOT NULL UNIQUE
);

CREATE TABLE species (
  id    INTEGER PRIMARY KEY,
  name  VARCHAR NOT NULL UNIQUE
);

CREATE TABLE planets (
  id      INTEGER PRIMARY KEY,
  name    VARCHAR NOT NULL UNIQUE,
  ecology VARCHAR NOT NULL
);

CREATE TABLE characters (
  id                  INTEGER PRIMARY KEY,
  kind                VARCHAR NOT NULL CHECK (kind in ('organic', 'droid')),
  name                VARCHAR NOT NULL UNIQUE,
  faction             INTEGER CHECK (faction in (1, 2)),
  favorite_weapon_id  INTEGER REFERENCES weapons (id),
  UNIQUE (id, kind)
);

CREATE TABLE characters_organics (
  id              INTEGER PRIMARY KEY REFERENCES characters (id),
  kind            VARCHAR DEFAULT 'organic' CHECK (kind = 'organic'),
  species_id      INTEGER NOT NULL REFERENCES species (id),
  home_planet_id  INTEGER NOT NULL REFERENCES planets (id),
  FOREIGN KEY (id, kind) REFERENCES characters (id, kind)
);

CREATE TABLE characters_droids (
  id                INTEGER PRIMARY KEY REFERENCES characters (id),
  kind              VARCHAR DEFAULT 'droid' CHECK (kind = 'droid'),
  primary_function  INTEGER NOT NULL CHECK (primary_function in (1, 2, 3)),
  FOREIGN KEY (id, kind) REFERENCES characters (id, kind)
);

CREATE TABLE character_associations (
  id        SERIAL PRIMARY KEY,
  target_id INTEGER NOT NULL REFERENCES characters (id),
  source_id INTEGER NOT NULL REFERENCES characters (id),
  relation  VARCHAR NOT NULL,
  CHECK (target_id != source_id),
  UNIQUE (target_id, source_id)
);

CREATE TABLE character_episodes (
  character_id  INTEGER NOT NULL REFERENCES characters (id),
  episode       INTEGER NOT NULL CHECK (episode in (1, 2, 3)),
  UNIQUE (character_id, episode)
);

CREATE TABLE character_comments (
  id            SERIAL PRIMARY KEY,
  commenter_id  INTEGER NOT NULL REFERENCES characters (id),
  commentee_id  INTEGER NOT NULL REFERENCES characters (id),
  reply_to_id   INTEGER REFERENCES character_comments (id),
  comment       VARCHAR NOT NULL
);

-- Data

INSERT INTO weapons (id, name) VALUES
  (1, 'Lightsaber'),
  (2, 'DL-44 blaster'),
  (3, 'Bowcaster'),
  (4, 'Blaster rifle');

INSERT INTO species (id, name) VALUES
  (1, 'Human'),
  (2, 'Wookiee'),
  (3, 'Mon Calamari'),
  (4, 'Sullustan');

INSERT INTO planets (id, name, ecology) VALUES
  (1, 'Tatooine', 'Desert'),
  (2, 'Alderaan', 'Temperate'),
  (3, 'Corellia', 'Urban'),
  (4, 'Kashyyyk', 'Forested'),
  (5, 'Socorro', 'Desert'),
  (6, 'Dac', 'Aquatic'),
  (7, 'Sullust', 'Volcanic'),
  (8, 'Naboo', 'Temperate'),
  (9, 'Kamino', 'Aquatic');

INSERT INTO characters (id, kind, name, faction, favorite_weapon_id) VALUES
  (1, 'organic', 'Luke Skywalker', 1, 1),
  (2, 'organic', 'Leia Organa', 1, null),
  (3, 'organic', 'Han Solo', 1, 2),
  (4, 'organic', 'Chewbacca', 1, 3),
  (5, 'organic', 'Lando Calrissian', 1, null),
  (6, 'organic', 'Gial Ackbar', 1, null),
  (7, 'organic', 'Nien Nunb', 1, null),
  (8, 'organic', 'Palpatine', 2, null),
  (9, 'organic', 'Darth Vader', 2, 1),
  (10, 'organic', 'Boba Fett', null, 4),
  (11, 'droid', 'R2-D2', 1, null),
  (12, 'droid', 'C-3PO', 1, null),
  (13, 'droid', 'IG-88', null, 4);

INSERT INTO characters_organics (id, species_id, home_planet_id) VALUES
  (1, 1, 1), -- Luke
  (2, 1, 2), -- Leia
  (3, 1, 3), -- Han
  (4, 2, 4), -- Chewie
  (5, 1, 5), -- Lando
  (6, 3, 6), -- Ackbar
  (7, 4, 7), -- Nien
  (8, 1, 8), -- Palpatine
  (9, 1, 1), -- Vader
  (10, 1, 9); -- Boba

INSERT INTO characters_droids (id, primary_function) VALUES
  (11, 1), -- R2
  (12, 2), -- 3PO
  (13, 3); -- IG

INSERT INTO character_episodes (character_id, episode) VALUES
  (1, 1), (1, 2), (1, 3), -- Luke
  (2, 1), (2, 2), (2, 3), -- Leia
  (3, 1), (3, 2), (3, 3), -- Han
  (4, 1), (4, 2), (4, 3), -- Chewie
          (5, 2), (5, 3), -- Lando
                  (6, 3), -- Ackbar
                  (7, 3), -- Nien
          (8, 2), (8, 3), -- Palpatine
  (9, 1), (9, 2), (9, 3), -- Vader
          (10, 2), (10, 3), -- Boba
  (11, 1), (11, 2), (11, 3), -- R2
  (12, 1), (12, 2), (12, 3); -- 3PO

INSERT INTO character_associations (target_id, source_id, relation) VALUES
  (1, 2, 'sister'), (1, 3, 'friend'), (1, 4, 'friend'), (1, 5, 'friend'), (1, 9, 'father'), (1, 11, 'friend'), (1, 12, 'friend'), -- Luke
  (2, 1, 'brother'), (2, 3, 'lover'), (2, 4, 'friend'), (2, 5, 'friend'), (2, 9, 'father'), (2, 11, 'friend'), (2, 12, 'friend'), -- Leia
  (3, 1, 'friend'), (3, 2, 'lover'), (3, 4, 'friend'), (3, 5, 'friend'), (3, 11, 'friend'), (3, 12, 'friend'), -- Han
  (4, 1, 'friend'), (4, 2, 'friend'), (4, 3, 'friend'), (4, 5, 'friend'), (4, 11, 'friend'), (4, 12, 'friend'), -- Chewie
  (5, 1, 'friend'), (5, 2, 'friend'), (5, 3, 'friend'), (5, 4, 'friend'), (5, 7, 'co-pilot'), (5, 11, 'friend'), (5, 12, 'friend'), -- Lando
  (6, 7, 'brother-in-arms'), -- Ackbar
  (7, 6, 'brother-in-arms'), -- Nien
  (8, 9, 'apprentice'), -- Palpatine
  (9, 8, 'master'), (9, 10, 'contractor'), (9, 1, 'son'), (9, 2, 'daughter'), -- Vader
  (10, 9, 'patron'), -- Boba
  (11, 1, 'friend'), (11, 2, 'friend'), (11, 3, 'friend'), (11, 4, 'friend'), (11, 5, 'friend'), (11, 12, 'friend'), -- R2
  (12, 1, 'friend'), (12, 2, 'friend'), (12, 3, 'friend'), (12, 4, 'friend'), (12, 5, 'friend'), (12, 11, 'friend'); -- 3PO
