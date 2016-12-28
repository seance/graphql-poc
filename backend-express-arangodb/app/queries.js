import { aql } from 'arangojs'
import db from './database'
import R from 'ramda'

const getAll = (query, f) =>
  db.query(query).then(cursor => cursor.all()).then(f || R.identity)

const getOne = (query, f) =>
  db.query(query).then(cursor => cursor.next()).then(f || R.identity)

const queryAllPlanets = () => aql`
  FOR p IN planets RETURN p`

const queryAllSpecies = () => aql`
  FOR s IN species RETURN s`

const queryAllCharacters = () => aql`
  FOR c IN characters RETURN MERGE(c,
    (FOR v IN OUTBOUND c favorite_weapon RETURN { favoriteWeapon: v })[0] || {},
    (FOR v IN OUTBOUND c home_planet RETURN { homePlanet: v })[0] || {},
    (FOR v IN OUTBOUND c is_species RETURN { species: v })[0] || {},
    { associations: (FOR a, e IN INBOUND c association RETURN {
        id: e.id,
        targetId: c.id,
        sourceId: a.id,
        relation: e.relation
      })
    }
  )`

const queryCharacter = (id) => aql`
  FOR c IN characters FILTER c.id == ${id} RETURN MERGE(c,
    (FOR v IN OUTBOUND c favorite_weapon RETURN { favoriteWeapon: v })[0] || {},
    (FOR v IN OUTBOUND c home_planet RETURN { homePlanet: v })[0] || {},
    (FOR v IN OUTBOUND c is_species RETURN { species: v })[0] || {},
    { associations: (FOR a, e IN INBOUND c association RETURN {
        id: e.id,
        targetId: c.id,
        sourceId: a.id,
        relation: e.relation
      })
    }
  )`

const queryPlanet = (id) => aql`
  FOR p IN planets FILTER p.id == ${id} RETURN p`

const querySpecies1 = (id) => aql`
  FOR s IN species FILTER s.id == ${id} RETURN s`

const queryPlanetsBySpecies = (speciesId) => aql`
  FOR s IN species FILTER s.id == ${speciesId} RETURN UNIQUE(FLATTEN(
    FOR c IN INBOUND s is_species RETURN (
      FOR p IN OUTBOUND c home_planet RETURN p
    )
  ))`

const queryCharactersBySpecies = (speciesId) => aql`
  FOR s IN species FILTER s.id == ${speciesId} RETURN FLATTEN(
    FOR c IN INBOUND s is_species RETURN MERGE(c,
      (FOR v IN OUTBOUND c favorite_weapon RETURN { favoriteWeapon: v })[0] || {},
      (FOR v IN OUTBOUND c home_planet RETURN { homePlanet: v })[0] || {},
      (FOR v IN OUTBOUND c is_species RETURN { species: v })[0] || {},
      { associations: (FOR a, e IN INBOUND c association RETURN {
          id: e.id,
          targetId: c.id,
          sourceId: a.id,
          relation: e.relation
        })
      }
    )
  )`

const querySpeciesByPlanet = (planetId) => aql`
  FOR p IN planets FILTER p.id == ${planetId} RETURN UNIQUE(FLATTEN(
    FOR c IN INBOUND p home_planet RETURN (
      FOR s IN OUTBOUND c is_species RETURN s
    )
  ))`

const queryNativesByPlanet = (planetId) => aql`
  FOR p IN planets FILTER p.id == ${planetId} RETURN (
    FOR c IN INBOUND p home_planet RETURN MERGE(c,
      (FOR v IN OUTBOUND c favorite_weapon RETURN { favoriteWeapon: v })[0] || {},
      (FOR v IN OUTBOUND c home_planet RETURN { homePlanet: v })[0] || {},
      (FOR v IN OUTBOUND c is_species RETURN { species: v })[0] || {},
      { associations: (FOR a, e IN INBOUND c association RETURN {
          id: e.id,
          targetId: c.id,
          sourceId: a.id,
          relation: e.relation
        })
      }
    )
  )`

const queryCharactersByIds = (characterIds) => aql`
  FOR c IN characters FILTER c.id IN ${characterIds} RETURN MERGE(c,
    (FOR v IN OUTBOUND c favorite_weapon RETURN { favoriteWeapon: v })[0] || {},
    (FOR v IN OUTBOUND c home_planet RETURN { homePlanet: v })[0] || {},
    (FOR v IN OUTBOUND c is_species RETURN { species: v })[0] || {},
    { associations: (FOR a, e IN INBOUND c association RETURN {
        id: e.id,
        targetId: c.id,
        sourceId: a.id,
        relation: e.relation
      })
    }
  )`

export const findAllCharacters = () =>
  getAll(queryAllCharacters())

export const findAllPlanets = () =>
  getAll(queryAllPlanets())

export const findAllSpecies = () =>
  getAll(queryAllSpecies())

export const findCharacter = characterId =>
  getOne(queryCharacter(characterId))

export const findPlanet = planetId =>
  getOne(queryPlanet(planetId))

export const findSpecies1 = speciesId =>
  getOne(querySpecies1(speciesId))

export const findPlanetsBySpecies = speciesId =>
  getAll(queryPlanetsBySpecies(speciesId), R.head)

export const findCharactersBySpecies = speciesId =>
  getAll(queryCharactersBySpecies(speciesId), R.head)

export const findSpeciesByPlanet = planetId =>
  getAll(querySpeciesByPlanet(planetId), R.head)

export const findNativesByPlanet = planetId =>
  getAll(queryNativesByPlanet(planetId), R.head)

export const findCharactersByIds = characterIds =>
  getAll(queryCharactersByIds(characterIds))
