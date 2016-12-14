import React, { Component, PropTypes } from 'react'
import { connect } from 'react-redux'

import { fetchGraphQL } from '../actions/graphql'
import * as Queries from '../queries'
import CharacterList from '../components/characterList'

class Home extends Component {

  static propTypes = {
    isFetching: PropTypes.bool.isRequired,
    characters: PropTypes.arrayOf(PropTypes.object).isRequired,
    withAssociates: PropTypes.bool.isRequired
  }

  componentDidMount() {
    const { fetchGraphQL, withAssociates } = this.props
    fetchGraphQL(Queries.characters, { withAssociates })
  }

  render() {
    const { isFetching, characters, withAssociates } = this.props
    return (
      <div>
        This is Home
        {
          isFetching ? (
            <span>Loading...</span>
          ) : (
            <CharacterList
              characters={characters}
              withAssociates={withAssociates}/>
          )
        }
      </div>
    )
  }
}

export default connect(state => ({
  // mapStateToProps
  ...state.home
}), {
  // mapDispatchToProps
  fetchGraphQL
})(Home)
