import React from 'react'
import {connect} from 'react-redux'
import {toggleAudioPause, playListSkip} from '../actions'
import {PlayButton, SkipPrevButton, SkipNextButton} from './presentational/player-buttons'

function mapStateToProps(state){
    return {
        isPaused: state.audioPaused
    };
}

function mapDispatchToProps(dispatch) {
    return {
        handleClick: function(){
            dispatch(toggleAudioPause())
        }
    }
}

const  ReduxPlayButton = connect(
    (state)=>{return {isPaused: state.audioPaused};},
    (dispatch)=>{return {handleClick: ()=>dispatch(toggleAudioPause())}}
)(PlayButton);

function skipDispatch(isForvard){
    return function(dispatch){
        return {
            handleClick: () => dispatch(playListSkip(isForvard))
        }
    }
}

const ReduxSkipNext = connect(
    null,
    skipDispatch(true)
)(SkipNextButton);

const ReduxSkipPrev = connect(
    null,
    skipDispatch(false)
)(SkipPrevButton);

export {
    ReduxPlayButton as PlayButton,
    ReduxSkipNext as SkipNextButton,
    ReduxSkipPrev as SkipPrevButton
};