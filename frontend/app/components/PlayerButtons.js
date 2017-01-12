import {connect} from "react-redux";

import * as P from "./presentational/PlayerButtons"
import {requestPause, requestPlay, playPrev, playNext} from "../actions"

const PlayPauseButton = connect(
    function mapStateToProps(state) {
        const playlist = state.player.playlist;
        return {
            active: playlist.currentPos < playlist.tracks.length,
            isPaused: !state.player.isPlaying
        };
    },
    function mapDispatchToProps(dispatch) {
        return {
            onPlay: function () {
                dispatch(requestPlay())
            },
            onPause: function () {
                dispatch(requestPause())
            }
        };
    }
)(P.PlayPauseButton);

const SkipNextButton = connect(
    function mapStateToProps(state) {
        const playlist = state.player.playlist;
        return {
            active: playlist.currentPos + 1 < playlist.tracks.length
        };
    },
    function mapDispatchToProps(dispatch) {
        return {
            onNext: ()=>dispatch(playNext())
        };
    }
)(P.SkipNextButton);

const SkipPrevButton = connect(
    function mapStateToProps(state) {
        const playlist = state.player.playlist;
        return {
            active: playlist.currentPos  > 0
        };
    },
    function mapDispatchToProps(dispatch) {
        return {
            onPrev: ()=>dispatch(playPrev())
        };
    }
)(P.SkipPrevButton);

export {PlayPauseButton, SkipNextButton, SkipPrevButton}