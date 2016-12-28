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

import DataLoader from 'dataloader'
import R from 'ramda'
import * as q from './queries'

export const charLoader = () =>
  new DataLoader(ids => q.findCharactersByIds(ids).then(rows =>
    ids.map(id => R.find(R.propEq('id', id))(rows))))

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
    id: { type: GraphQLID, resolve: (root) => root.id.toString() },
    name: { type: GraphQLString }
  })
})

const SpeciesType = new GraphQLObjectType({
  name: 'Species',
  fields: () => ({
    id: { type: GraphQLID, resolve: (root) => root.id.toString() },
    name: { type: GraphQLString },
    foundOn: {
      type: new GraphQLList(PlanetType),
      resolve: (root) =>
        q.findPlanetsBySpecies(root.id)
    },
    notableMembers: {
      type: new GraphQLList(OrganicType),
      resolve: (root) =>
        q.findCharactersBySpecies(root.id)
    }
  })
})

const PlanetType = new GraphQLObjectType({
  name: 'Planet',
  fields: () => ({
    id: { type: GraphQLID, resolve: (root) => root.id.toString() },
    name: { type: GraphQLString },
    ecology: { type: GraphQLString },
    species: {
      type: new GraphQLList(SpeciesType),
      resolve: (root) =>
        q.findSpeciesByPlanet(root.id)
    },
    natives: {
      type: new GraphQLList(OrganicType),
      resolve: (root) =>
        q.findNativesByPlanet(root.id)
    }
  })
})

const AssociationType = new GraphQLObjectType({
  name: 'Association',
  fields: () => ({
    id: { type: GraphQLID, resolve: (root) => root.id.toString() },
    relation: { type: GraphQLString },
    character: {
      type: CharacterType,
      resolve: (root, args, context, info) =>
        context.charLoader.load(root.sourceId)
    }
  })
})

const characterCommonFields = {
  id: { type: GraphQLID, resolve: (root) => root.id.toString() },
  kind: { type: GraphQLString },
  name: { type: GraphQLString },
  faction: { type: FactionType },
  favoriteWeapon: { type: WeaponType },
  associates: {
    type: new GraphQLList(AssociationType),
    resolve: (root) => root.associations
  }
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
  fields: () => R.merge(characterCommonFields, {
    species: { type: SpeciesType },
    homePlanet: { type: PlanetType }
  })
})

const DroidType = new GraphQLObjectType({
  name: 'Droid',
  interfaces: [CharacterType],
  fields: () => R.merge(characterCommonFields, {
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
    },
    character: {
      type: CharacterType,
      args: {
        characterId: { type: GraphQLID }
      },
      resolve: (root, args, context, info) =>
        q.findCharacter(parseInt(args.characterId, 10))
    },
    planet: {
      type: PlanetType,
      args: {
        planetId: { type: GraphQLID }
      },
      resolve: (root, args, context, info) =>
        q.findPlanet(parseInt(args.planetId, 10))
    },
    species1: {
      type: SpeciesType,
      args: {
        speciesId: { type: GraphQLID }
      },
      resolve: (root, args, context, info) =>
        q.findSpecies1(parseInt(args.speciesId, 10))
    }
  })
})

export const schema = new GraphQLSchema({
  types: [OrganicType, DroidType],
  query: QueryType
})
