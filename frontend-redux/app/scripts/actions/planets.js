import * as Queries from '../queries'
import { fetchGraphQL } from './graphql'

export const FetchPlanets = {
  Request: 'FETCH_PLANETS_REQUEST',
  Success: 'FETCH_PLANETS_SUCCESS',
  Failure: 'FETCH_PLANETS_FAILURE'
}

export const fetchPlanets = variables =>
  fetchGraphQL(FetchPlanets, Queries.FetchPlanets, variables)
