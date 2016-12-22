import React, { Component, PropTypes as P } from 'react'
import { Link } from 'react-router'
import { connect } from 'react-redux'

import { selectBackend } from '../actions/app'

class App extends Component {

  static propTypes = {
  }

  render() {
    const { location } = this.props
    const active = {
      home: location.pathname === '/' ? 'is-active' : '',
      planets: location.pathname === '/planets' ? 'is-active' : '',
      species: location.pathname === '/species' ? 'is-active' : ''
    }
    return (
      <div>
        <header className="header">
          <div className="header-title">
            <h1>
              Star Wars
              <span className="by"> by </span>
            </h1>
            <a href="https://facebook.github.io/react/" target="_blank">
              <div className="header-icon icon-react"></div>
            </a>
            <a href="http://redux.js.org/" target="_blank">
              <div className="header-icon icon-redux"></div>
            </a>
            <a href="http://graphql.org/" target="_blank">
              <div className="header-icon icon-graphql"></div>
            </a>
          </div>
          <div className="header-backend">
            <div className="backend-label">
              Backend stack
            </div>
            <select onChange={this.selectBackend.bind(this)}>
              <option value="backend1">JVM + Relational</option>
              <option value="backend2">JVM + Graph</option>
              <option value="backend3" disabled>Node + Graph</option>
            </select>
          </div>
          <nav>
            <ul>
              <li><Link to={'/'} className={active.home}>Home</Link></li>
              <li><Link to={'/planets'} className={active.planets}>Planets</Link></li>
              <li><Link to={'/species'} className={active.species}>Species</Link></li>
            </ul>
          </nav>
        </header>
        <div>
          {this.props.children}
        </div>
      </div>
    )
  }

  selectBackend(e) {
    this.props.selectBackend(e.target.value)
  }
}

export default connect(state => ({
  // mapStateToProps
  ...state.router
}), {
  // mapDispatchToProps
  selectBackend
})(App)
