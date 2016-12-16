import React, { Component, PropTypes as P } from 'react'
import R from 'ramda'

export default class Character extends Component {

  static propTypes = {
    character: P.object.isRequired,
  }

  render() {
    const { character: c } = this.props
    return c.kind === 'organic'
      ? this.renderOrganic(c)
      : this.renderDroid(c)
  }

  renderOrganic(c) {
    return (
      <div className="character character-organic">
        <div className="species">{c.species.name}</div>
        <div className="name">{c.name}</div>
        <div className="favorite-weapon">
          {R.pathOr('No favorite weapon', ['favoriteWeapon', 'name'], c)}
        </div>
        <div className="planet">{c.homePlanet.name}</div>
      </div>
    )
  }

  renderDroid(c) {
    return (
      <div className="character character-droid">
        <div className="droid">Droid</div>
        <div className="name">{c.name}</div>
        <div className="favorite-weapon">
          {R.pathOr('No favorite weapon', ['favoriteWeapon', 'name'], c)}
        </div>
        <div className="primary-function">{c.primaryFunction}</div>
      </div>
    )
  }

  /*
  CHARACTER:
  <kind-icon>    <FULL NAME>
  <kind/species> Label: <weapon-icon> <weapon-name>
                 Label: <planet-name/primaryFunction>

  PLANET:
  <planet-icon> <NAME>
                Label: <ecology>
  */
}
