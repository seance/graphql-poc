import * as Queries from '../queries'
import { fetchGraphQL } from './graphql'

export const FetchSpecies = {
  Request: 'FETCH_SPECIES_REQUEST',
  Success: 'FETCH_SPECIES_SUCCESS',
  Failure: 'FETCH_SPECIES_FAILURE'
}

export const fetchSpecies = variables =>
  fetchGraphQL(FetchSpecies, Queries.FetchSpecies, variables)
