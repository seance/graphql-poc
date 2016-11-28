var genGraph = require('@arangodb/general-graph');

db._createDatabase('pocdb');
db._useDatabase('pocdb');

var g = genGraph._create('poc-graph');
g._addVertexCollection("foos", true);

g.foos.save({ bar: "hello", zut: 42 });
g.foos.save({ bar: "express/arangodb", zut: 13 });
