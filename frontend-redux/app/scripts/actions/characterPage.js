import * as Queries from '../queries'
import { fetchGraphQL } from './graphql'

export const FetchCharacterAndAssocs = {
  Request: 'FETCH_CHARACTER_AND_ASSOCS_REQUEST',
  Success: 'FETCH_CHARACTER_AND_ASSOCS_SUCCESS',
  Failure: 'FETCH_CHARACTER_AND_ASSOCS_FAILURE'
}

export const fetchCharacterAndAssocs = variables =>
  fetchGraphQL(FetchCharacterAndAssocs, Queries.FetchCharacterAndAssocs, variables)
