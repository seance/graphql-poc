import * as Queries from '../queries'
import { fetchGraphQL } from './graphql'

export const FetchCharacters = {
  Request: 'FETCH_CHARACTERS_REQUEST',
  Success: 'FETCH_CHARACTERS_SUCCESS',
  Failure: 'FETCH_CHARACTERS_FAILURE'
}

export const fetchCharacters = variables =>
  fetchGraphQL(FetchCharacters, Queries.FetchCharacters, variables)
