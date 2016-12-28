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

var lightsaber = g.weapons.save({ id: 1, name: 'Lightsaber' })._id;
var dl44 = g.weapons.save({ id: 2, name: 'DL-44 blaster'})._id;
var bowcaster = g.weapons.save({ id: 3, name: 'Bowcaster' })._id;
var blasterRifle = g.weapons.save({ id: 4, name: 'Blaster rifle' })._id;

var human = g.species.save({ id: 1, name: 'Human' })._id;
var wookiee = g.species.save({ id: 2, name: 'Wookiee' })._id;
var monCalamari = g.species.save({ id: 3, name: 'Mon Calamari' })._id;
var sullustan = g.species.save({ id: 4, name: 'Sullustan' })._id;

var tatooine = g.planets.save({ id: 1, name: 'Tatooine', ecology: 'Desert' })._id;
var alderaan = g.planets.save({ id: 2, name: 'Alderaan', ecology: 'Temperate' })._id;
var corellia = g.planets.save({ id: 3, name: 'Corellia', ecology: 'Urban' })._id;
var kashyyyk = g.planets.save({ id: 4, name: 'Kashyyyk', ecology: 'Forested' })._id;
var socorro = g.planets.save({ id: 5, name: 'Socorro', ecology: 'Desert' })._id;
var dac = g.planets.save({ id: 6, name: 'Dac', ecology: 'Aquatic' })._id;
var sullust = g.planets.save({ id: 7, name: 'Sullust', ecology: 'Volcanic' })._id;
var naboo = g.planets.save({ id: 8, name: 'Naboo', ecology: 'Temperate' })._id;
var kamino = g.planets.save({ id: 9, name: 'Kamino', ecology: 'Aquatic' })._id;

var luke = g.characters.save({ id: 1, name: 'Luke Skywalker', kind: 'organic', faction: 1 })._id;
var leia = g.characters.save({ id: 2, name: 'Leia Organa', kind: 'organic', faction: 1 })._id;
var han = g.characters.save({ id: 3, name: 'Han Solo', kind: 'organic', faction: 1 })._id;
var chewie = g.characters.save({ id: 4, name: 'Chewbacca', kind: 'organic', faction: 1 })._id;
var lando = g.characters.save({ id: 5, name: 'Lando Calrissian', kind: 'organic', faction: 1 })._id;
var ackbar = g.characters.save({ id: 6, name: 'Gial Ackbar', kind: 'organic', faction: 1 })._id;
var nunb = g.characters.save({ id: 7, name: 'Nien Nunb', kind: 'organic', faction: 1 })._id;
var palpatine = g.characters.save({ id: 8, name: 'Palpatine', kind: 'organic', faction: 2 })._id;
var vader = g.characters.save({ id: 9, name: 'Darth Vader', kind: 'organic', faction: 2 })._id;
var boba = g.characters.save({ id: 10, name: 'Boba Fett', kind: 'organic' })._id;
var r2d2 = g.characters.save({ id: 11, name: 'R2-D2', kind: 'droid', faction: 1, primaryFunction: 1 })._id;
var c3po = g.characters.save({ id: 12, name: 'C-3PO', kind: 'droid', faction: 1, primaryFunction: 2 })._id;
var ig88 = g.characters.save({ id: 13, name: 'IG-88', kind: 'droid', primaryFunction: 3 })._id;

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

g.association.save(luke, leia, { id: 1, relation: 'brother' });
g.association.save(luke, han, { id: 2, relation: 'friend' });
g.association.save(luke, chewie, { id: 3, relation: 'friend' });
g.association.save(luke, lando, { id: 4, relation: 'friend' });
g.association.save(luke, vader, { id: 5, relation: 'son' });
g.association.save(luke, r2d2, { id: 6, relation: 'friend' });
g.association.save(luke, c3po, { id: 7, relation: 'friend' });
g.association.save(leia, luke, { id: 8, relation: 'sister' });
g.association.save(leia, han, { id: 9, relation: 'lover' });
g.association.save(leia, chewie, { id: 10, relation: 'friend' });
g.association.save(leia, lando, { id: 11, relation: 'friend' });
g.association.save(leia, vader, { id: 12, relation: 'daughter' });
g.association.save(leia, r2d2, { id: 13, relation: 'friend' });
g.association.save(leia, c3po, { id: 14, relation: 'friend' });
g.association.save(han, luke, { id: 15, relation: 'friend' });
g.association.save(han, leia, { id: 16, relation: 'lover' });
g.association.save(han, chewie, { id: 17, relation: 'friend' });
g.association.save(han, lando, { id: 18, relation: 'friend' });
g.association.save(han, r2d2, { id: 19, relation: 'friend' });
g.association.save(han, c3po, { id: 20, relation: 'friend' });
g.association.save(chewie, luke, { id: 21, relation: 'friend' });
g.association.save(chewie, leia, { id: 22, relation: 'friend' });
g.association.save(chewie, han, { id: 23, relation: 'friend' });
g.association.save(chewie, lando, { id: 24, relation: 'friend' });
g.association.save(chewie, r2d2, { id: 25, relation: 'friend' });
g.association.save(chewie, c3po, { id: 26, relation: 'friend' });
g.association.save(lando, luke, { id: 27, relation: 'friend' });
g.association.save(lando, leia, { id: 28, relation: 'friend' });
g.association.save(lando, han, { id: 29, relation: 'friend' });
g.association.save(lando, chewie, { id: 30, relation: 'friend' });
g.association.save(lando, nunb, { id: 31, relation: 'co-pilot' });
g.association.save(lando, r2d2, { id: 32, relation: 'friend' });
g.association.save(lando, c3po, { id: 33, relation: 'friend' });
g.association.save(ackbar, nunb, { id: 34, relation: 'brother-in-arms' });
g.association.save(nunb, ackbar, { id: 35, relation: 'brother-in-arms' });
g.association.save(nunb, lando, { id: 36, relation: 'co-pilot' });
g.association.save(palpatine, vader, { id: 37, relation: 'master' });
g.association.save(vader, luke, { id: 38, relation: 'father' });
g.association.save(vader, leia, { id: 39, relation: 'father' });
g.association.save(vader, palpatine, { id: 40, relation: 'apprentice' });
g.association.save(vader, boba, { id: 41, relation: 'patron' });
g.association.save(boba, vader, { id: 42, relation: 'contractor' });
g.association.save(r2d2, luke, { id: 43, relation: 'friend' });
g.association.save(r2d2, leia, { id: 44, relation: 'friend' });
g.association.save(r2d2, han, { id: 45, relation: 'friend' });
g.association.save(r2d2, chewie, { id: 46, relation: 'friend' });
g.association.save(r2d2, lando, { id: 47, relation: 'friend' });
g.association.save(r2d2, c3po, { id: 48, relation: 'friend' });
g.association.save(c3po, luke, { id: 49, relation: 'friend' });
g.association.save(c3po, leia, { id: 50, relation: 'friend' });
g.association.save(c3po, han, { id: 51, relation: 'friend' });
g.association.save(c3po, chewie, { id: 52, relation: 'friend' });
g.association.save(c3po, lando, { id: 53, relation: 'friend' });
g.association.save(c3po, r2d2, { id: 54, relation: 'friend' });
