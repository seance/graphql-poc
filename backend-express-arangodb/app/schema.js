import {
  GraphQLSchema,
  GraphQLEnumType,
  GraphQLInterfaceType,
  GraphQLObjectType,
  GraphQLList,
  GraphQLID,
  GraphQLString,
  GraphQLInt
} from 'graphql'

import * as q from './queries'

const FactionType = new GraphQLEnumType({
  name: 'Faction',
  description: 'Rebel Alliance or Galactic Empire',
  values: {
    RebelAlliance: { name: 'RebelAlliance', value: 1 },
    GalacticEmpire: { name: 'GalacticEmpire', value: 2 }
  }
})

const DroidFunctionType = new GraphQLEnumType({
  name: 'DroidFunction',
  description: 'Primary function of a droid',
  values: {
    Astromech: { name: 'Astromech', value: 1 },
    Protocol: { name: 'Protocol', value: 2 },
    Assassin: { name: 'Assassin', value: 3 }
  }
})

const WeaponType = new GraphQLObjectType({
  name: 'Weapon',
  fields: () => ({
    id: { type: GraphQLID, resolve: (root) => root._key },
    name: { type: GraphQLString }
  })
})

const SpeciesType = new GraphQLObjectType({
  name: 'Species',
  fields: () => ({
    id: { type: GraphQLID, resolve: (root) => root._key },
    name: { type: GraphQLString }
  })
})

const PlanetType = new GraphQLObjectType({
  name: 'Planet',
  fields: () => ({
    id: { type: GraphQLID, resolve: (root) => root._key },
    name: { type: GraphQLString },
    ecology: { type: GraphQLString }
  })
})

const AssociationType = new GraphQLObjectType({
  name: 'Association',
  fields: () => ({
    id: { type: GraphQLID, resolve: (root) => root._key },
    relation: { type: GraphQLString }
  })
})

const characterCommonFields = {
  id: { type: GraphQLID, resolve: (root) => root._key },
  kind: { type: GraphQLString },
  name: { type: GraphQLString },
  favoriteWeapon: { type: WeaponType }
}

const CharacterType = new GraphQLInterfaceType({
  name: 'Character',
  fields: () => characterCommonFields,
  resolveType: (value, info) =>
    (value.kind === 'organic') ? OrganicType : DroidType
})

const OrganicType = new GraphQLObjectType({
  name: 'Organic',
  interfaces: [CharacterType],
  fields: () => Object.assign({}, characterCommonFields, {
    species: { type: SpeciesType },
    homePlanet: { type: PlanetType }
  })
})

const DroidType = new GraphQLObjectType({
  name: 'Droid',
  interfaces: [CharacterType],
  fields: () => Object.assign({}, characterCommonFields, {
    primaryFunction: { type: DroidFunctionType }
  })
})

const QueryType = new GraphQLObjectType({
  name: 'Query',
  fields: () => ({
    characters: {
      type: new GraphQLList(CharacterType),
      resolve: (root, args, context, info) =>
        q.findAllCharacters()
    },
    planets: {
      type: new GraphQLList(PlanetType),
      resolve: (root, args, context, info) =>
        q.findAllPlanets()
    },
    species: {
      type: new GraphQLList(SpeciesType),
      resolve: (root, args, context, info) =>
        q.findAllSpecies()
    }
  })
})

export default new GraphQLSchema({
  types: [OrganicType, DroidType],
  query: QueryType
})
