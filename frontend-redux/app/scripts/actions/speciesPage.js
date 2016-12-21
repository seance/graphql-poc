import * as Queries from '../queries'
import { fetchGraphQL } from './graphql'

export const FetchSpeciesAndRelated = {
  Request: 'FETCH_SPECIES_AND_RELATED_REQUEST',
  Success: 'FETCH_SPECIES_AND_RELATED_SUCCESS',
  Failure: 'FETCH_SPECIES_AND_RELATED_FAILURE'
}

export const fetchSpeciesAndRelated = variables =>
  fetchGraphQL(FetchSpeciesAndRelated, Queries.FetchSpeciesAndRelated, variables)
