import React from 'react'
import {connect} from 'react-redux'
import {toggleAudioPause} from '../actions'

function MyButton(props){
    return (
        <button className="btn waves-effect waves-light"
                onClick={props.handleClick}>
            {props.children}
        </button>
    )
}

function mapDispatchToProps(dispatch) {
    return {
        handleClick: function(){
            dispatch(toggleAudioPause())
        }
    }
}

const PlayButton = connect(null, mapDispatchToProps)(MyButton);
export default PlayButton;