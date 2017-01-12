import React from 'react'

function PlayerButton(props) {
    return (
        <button className="player-btn" onClick={props.handleClick} disabled={!props.active}>
            {props.children}
        </button>
    )
}
PlayerButton.propTypes = {
    active: React.PropTypes.bool,
    handleClick: React.PropTypes.func,
    children: React.PropTypes.element
};

function PlayPauseButton(props){
    return (
        <PlayerButton active={props.active} handleClick={props.isPaused? props.onPlay : props.onPause}>
            <i className="material-icons">{props.isPaused? "play_arrow" : "pause"}</i>
        </PlayerButton>
    )
}
PlayPauseButton.propTypes = {
    active: React.PropTypes.bool.isRequired,
    isPaused: React.PropTypes.bool.isRequired,
    onPlay: React.PropTypes.func.isRequired,
    onPause: React.PropTypes.func.isRequired
};

function SkipNextButton(props) {
    return (
        <PlayerButton active={props.active} handleClick={props.onNext}>
            <i className="material-icons">skip_next</i>
        </PlayerButton>
    )
}
SkipNextButton.propTypes = {
    active: React.PropTypes.bool.isRequired,
    onNext: React.PropTypes.func.isRequired
};

function SkipPrevButton(props) {
    return (
        <PlayerButton active={props.active} handleClick={props.onPrev}>
            <i className="material-icons">skip_previous</i>
        </PlayerButton>
    )
}
SkipPrevButton.propTypes = {
    active: React.PropTypes.bool.isRequired,
    onPrev: React.PropTypes.func.isRequired
};

export {PlayPauseButton, SkipNextButton, SkipPrevButton};