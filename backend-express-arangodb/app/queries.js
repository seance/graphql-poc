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
    { associations: (FOR v, e IN INBOUND c association RETURN e) }
  )`

const queryCharacter = (id) => aql`
  FOR c IN characters FILTER c._id == ${id} RETURN MERGE(c,
    (FOR v IN OUTBOUND c favorite_weapon RETURN { favoriteWeapon: v })[0] || {},
    (FOR v IN OUTBOUND c home_planet RETURN { homePlanet: v })[0] || {},
    (FOR v IN OUTBOUND c is_species RETURN { species: v })[0] || {},
    { associations: (FOR v, e IN INBOUND c association RETURN e) }
  )`

const queryPlanet = (id) => aql`
  FOR p IN planets FILTER p._id == ${id} RETURN p`

const querySpecies1 = (id) => aql`
  FOR s IN species FILTER s._id == ${id} RETURN s`

const queryPlanetsBySpecies = (speciesId) => aql`
  FOR s IN species FILTER s._id == ${speciesId} RETURN UNIQUE(FLATTEN(
    FOR c IN INBOUND s is_species RETURN (
      FOR p IN OUTBOUND c home_planet RETURN p
    )
  ))
  `

const queryCharactersBySpecies = (speciesId) => aql`
  FOR s IN species FILTER s._id == ${speciesId} RETURN FLATTEN(
    FOR c IN INBOUND s is_species RETURN MERGE(c,
      (FOR v IN OUTBOUND c favorite_weapon RETURN { favoriteWeapon: v })[0] || {},
      (FOR v IN OUTBOUND c home_planet RETURN { homePlanet: v })[0] || {},
      (FOR v IN OUTBOUND c is_species RETURN { species: v })[0] || {},
      { associations: (FOR v, e IN INBOUND c association RETURN e) }
    )
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
