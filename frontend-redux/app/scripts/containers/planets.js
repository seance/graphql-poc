import React, { Component, PropTypes as P } from 'react'
import { connect } from 'react-redux'

import { fetchPlanets } from '../actions/planets'
import PlanetList from '../components/planetList'

class Planets extends Component {

  static propTypes = {
    isFetching: P.bool.isRequired,
    planets   : P.arrayOf(P.object).isRequired
  }

  componentDidMount() {
    const { fetchPlanets } = this.props
    fetchPlanets()
  }

  render() {
    const { isFetching, planets } = this.props
    const content = isFetching
      ? this.renderFetching()
      : this.renderPlanetList(planets)

    return (
      <div className="planets">
        {content}
      </div>
    )
  }

  renderFetching() {
    return <div className="progress"></div>
  }

  renderPlanetList(planets) {
    return <PlanetList planets={planets}/>
  }
}

export default connect(state => ({
  // mapStateToProps
  ...state.planets.toJS()
}), {
  // mapDispatchToProps
  fetchPlanets
})(Planets)
