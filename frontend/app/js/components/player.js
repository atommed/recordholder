import {connect} from 'react-redux'
import Player from './presentational/player'

function mapStateToProps(state){
    return {
        pause: state.audioPaused
    }
}
export default connect(mapStateToProps)(Player);