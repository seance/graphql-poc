import React, { Component, PropTypes as P } from 'react'
import { connect } from 'react-redux'
import { push } from 'redux-router'

class App extends Component {

  static propTypes = {
    push: P.func.isRequired
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
          <nav>
            <ul>
              <li className={active.home} onClick={this.clickNav('/')}>Home</li>
              <li className={active.planets} onClick={this.clickNav('/planets')}>Planets</li>
              <li className={active.species} onClick={this.clickNav('/species')}>Species</li>
            </ul>
          </nav>
        </header>
        <div>
          {this.props.children}
        </div>
      </div>
    )
  }

  clickNav(page) {
    return e => {
      e.preventDefault()
      this.props.push(page)
    }
  }
}

export default connect(state => ({
  // mapStateToProps
  ...state.router
}), {
  // mapDispatchToProps
  push
})(App)
