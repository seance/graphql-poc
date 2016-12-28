import { aql } from 'arangojs'
import db from './database'
import R from 'ramda'

const queryAll = (query, f) =>
  db.query(query).then(cursor => cursor.all()).then(f || R.identity)

const queryAllCharacters = () => aql`
  FOR c IN characters RETURN MERGE(c,
    (FOR v IN OUTBOUND c favorite_weapon RETURN { favoriteWeapon: v })[0] || {},
    (FOR v IN OUTBOUND c home_planet RETURN { homePlanet: v })[0] || {},
    (FOR v IN OUTBOUND c is_species RETURN { species: v })[0] || {}
  )
`

export const findAllCharacters = () =>
  queryAll(queryAllCharacters())

export const findAllPlanets = () =>
  queryAll(aql`FOR p IN planets RETURN p`)

export const findAllSpecies = () =>
  queryAll(aql`FOR s IN species RETURN s`)
