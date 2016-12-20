import * as Queries from '../queries'
import { fetchGraphQL } from './graphql'

export const FetchPlanetAndRelated = {
  Request: 'FETCH_PLANET_AND_RELATED_REQUEST',
  Success: 'FETCH_PLANET_AND_RELATED_SUCCESS',
  Failure: 'FETCH_PLANET_AND_RELATED_FAILURE'
}

export const fetchPlanetAndRelated = variables =>
  fetchGraphQL(FetchPlanetAndRelated, Queries.FetchPlanetAndRelated, variables)
