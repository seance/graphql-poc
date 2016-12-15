import React, { Component, PropTypes as P } from 'react'
import { connect } from 'react-redux'

import { fetchCharacters } from '../actions/home'
import CharacterList from '../components/characterList'

class Home extends Component {

  static propTypes = {
    isFetching    : P.bool.isRequired,
    characters    : P.arrayOf(P.object).isRequired,
    withAssociates: P.bool.isRequired
  }

  componentDidMount() {
    const { fetchCharacters, withAssociates } = this.props
    fetchCharacters({ withAssociates })
  }

  render() {
    const { isFetching, characters, withAssociates } = this.props
    return (
      <div>
        This is Home
        {
          isFetching ? (
            <div>Loading...</div>
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
  ...state.home.toJS()
}), {
  // mapDispatchToProps
  fetchCharacters
})(Home)
