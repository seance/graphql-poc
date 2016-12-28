var Graph = require('@arangodb/general-graph');
var db = require('internal').db;

db._createDatabase('pocdb');
db._useDatabase('pocdb');

var g = Graph._create('pocgraph', [
  Graph._relation('favorite_weapon', 'characters', 'weapons'),
  Graph._relation('is_species', 'characters', 'species'),
  Graph._relation('home_planet', 'characters', 'planets'),
  Graph._relation('association', 'characters', 'characters')
]);

var lightsaber = g.weapons.save({ name: 'Lightsaber' })._id;
var dl44 = g.weapons.save({ name: 'DL-44 blaster'})._id;
var bowcaster = g.weapons.save({ name: 'Bowcaster' })._id;
var blasterRifle = g.weapons.save({ name: 'Blaster rifle' })._id;

var human = g.species.save({ name: 'Human' })._id;
var wookiee = g.species.save({ name: 'Wookiee' })._id;
var monCalamari = g.species.save({ name: 'Mon Calamari' })._id;
var sullustan = g.species.save({ name: 'Sullustan' })._id;

var tatooine = g.planets.save({ name: 'Tatooine', ecology: 'Desert' })._id;
var alderaan = g.planets.save({ name: 'Alderaan', ecology: 'Temperate' })._id;
var corellia = g.planets.save({ name: 'Corellia', ecology: 'Urban' })._id;
var kashyyyk = g.planets.save({ name: 'Kashyyyk', ecology: 'Forested' })._id;
var socorro = g.planets.save({ name: 'Socorro', ecology: 'Desert' })._id;
var dac = g.planets.save({ name: 'Dac', ecology: 'Aquatic' })._id;
var sullust = g.planets.save({ name: 'Sullust', ecology: 'Volcanic' })._id;
var naboo = g.planets.save({ name: 'Naboo', ecology: 'Temperate' })._id;
var kamino = g.planets.save({ name: 'Kamino', ecology: 'Aquatic' })._id;

var luke = g.characters.save({ name: 'Luke Skywalker', kind: 'organic', faction: 1 })._id;
var leia = g.characters.save({ name: 'Leia Organa', kind: 'organic', faction: 1 })._id;
var han = g.characters.save({ name: 'Han Solo', kind: 'organic', faction: 1 })._id;
var chewie = g.characters.save({ name: 'Chewbacca', kind: 'organic', faction: 1 })._id;
var lando = g.characters.save({ name: 'Lando Calrissian', kind: 'organic', faction: 1 })._id;
var ackbar = g.characters.save({ name: 'Gial Ackbar', kind: 'organic', faction: 1 })._id;
var nunb = g.characters.save({ name: 'Nien Nunb', kind: 'organic', faction: 1 })._id;
var palpatine = g.characters.save({ name: 'Palpatine', kind: 'organic', faction: 2 })._id;
var vader = g.characters.save({ name: 'Darth Vader', kind: 'organic', faction: 2 })._id;
var boba = g.characters.save({ name: 'Boba Fett', kind: 'organic' })._id;
var r2d2 = g.characters.save({ name: 'R2-D2', kind: 'droid', faction: 1, primaryFunction: 1 })._id;
var c3po = g.characters.save({ name: 'C-3PO', kind: 'droid', faction: 1, primaryFunction: 2 })._id;
var ig88 = g.characters.save({ name: 'IG-88', kind: 'droid', primaryFunction: 3 })._id;

g.favorite_weapon.save(luke, lightsaber, {});
g.favorite_weapon.save(han, dl44, {});
g.favorite_weapon.save(chewie, bowcaster, {});
g.favorite_weapon.save(vader, lightsaber, {});
g.favorite_weapon.save(boba, blasterRifle, {});
g.favorite_weapon.save(ig88, blasterRifle, {});

g.is_species.save(luke, human, {});
g.is_species.save(leia, human, {});
g.is_species.save(han, human, {});
g.is_species.save(chewie, wookiee, {});
g.is_species.save(lando, human, {});
g.is_species.save(ackbar, monCalamari, {});
g.is_species.save(nunb, sullustan, {});
g.is_species.save(palpatine, human, {});
g.is_species.save(vader, human, {});
g.is_species.save(boba, human, {});

g.home_planet.save(luke, tatooine, {});
g.home_planet.save(leia, alderaan, {});
g.home_planet.save(han, corellia, {});
g.home_planet.save(chewie, kashyyyk, {});
g.home_planet.save(lando, socorro, {});
g.home_planet.save(ackbar, dac, {});
g.home_planet.save(nunb, sullust, {});
g.home_planet.save(palpatine, naboo, {});
g.home_planet.save(vader, tatooine, {});
g.home_planet.save(boba, kamino, {});

g.association.save(luke, leia, { relation: 'brother' });
g.association.save(luke, han, { relation: 'friend' });
g.association.save(luke, chewie, { relation: 'friend' });
g.association.save(luke, lando, { relation: 'friend' });
g.association.save(luke, vader, { relation: 'son' });
g.association.save(luke, r2d2, { relation: 'friend' });
g.association.save(luke, c3po, { relation: 'friend' });

g.association.save(leia, luke, { relation: 'sister' });
g.association.save(leia, han, { relation: 'lover' });
g.association.save(leia, chewie, { relation: 'friend' });
g.association.save(leia, lando, { relation: 'friend' });
g.association.save(leia, vader, { relation: 'daughter' });
g.association.save(leia, r2d2, { relation: 'friend' });
g.association.save(leia, c3po, { relation: 'friend' });

g.association.save(han, luke, { relation: 'friend' });
g.association.save(han, leia, { relation: 'lover' });
g.association.save(han, chewie, { relation: 'friend' });
g.association.save(han, lando, { relation: 'friend' });
g.association.save(han, r2d2, { relation: 'friend' });
g.association.save(han, c3po, { relation: 'friend' });

g.association.save(chewie, luke, { relation: 'friend' });
g.association.save(chewie, leia, { relation: 'friend' });
g.association.save(chewie, han, { relation: 'friend' });
g.association.save(chewie, lando, { relation: 'friend' });
g.association.save(chewie, r2d2, { relation: 'friend' });
g.association.save(chewie, c3po, { relation: 'friend' });

g.association.save(lando, luke, { relation: 'friend' });
g.association.save(lando, leia, { relation: 'friend' });
g.association.save(lando, han, { relation: 'friend' });
g.association.save(lando, chewie, { relation: 'friend' });
g.association.save(lando, nunb, { relation: 'co-pilot' });
g.association.save(lando, r2d2, { relation: 'friend' });
g.association.save(lando, c3po, { relation: 'friend' });

g.association.save(ackbar, nunb, { relation: 'brother-in-arms' });

g.association.save(nunb, ackbar, { relation: 'brother-in-arms' });
g.association.save(nunb, lando, { relation: 'co-pilot' });

g.association.save(palpatine, vader, { relation: 'master' });

g.association.save(vader, luke, { relation: 'father' });
g.association.save(vader, leia, { relation: 'father' });
g.association.save(vader, palpatine, { relation: 'apprentice' });
g.association.save(vader, boba, { relation: 'patron' });

g.association.save(boba, vader, { relation: 'contractor' });

g.association.save(r2d2, luke, { relation: 'friend' });
g.association.save(r2d2, leia, { relation: 'friend' });
g.association.save(r2d2, han, { relation: 'friend' });
g.association.save(r2d2, chewie, { relation: 'friend' });
g.association.save(r2d2, lando, { relation: 'friend' });
g.association.save(r2d2, c3po, { relation: 'friend' });

g.association.save(c3po, luke, { relation: 'friend' });
g.association.save(c3po, leia, { relation: 'friend' });
g.association.save(c3po, han, { relation: 'friend' });
g.association.save(c3po, chewie, { relation: 'friend' });
g.association.save(c3po, lando, { relation: 'friend' });
g.association.save(c3po, r2d2, { relation: 'friend' });
