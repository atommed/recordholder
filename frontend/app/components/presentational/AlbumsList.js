import React from 'react'
import {Link} from 'react-router'

function renderAlbum(album){
    return (
        <div className="albums-box-element" key={album.id}>
            { album.coverId  &&
            <img src={`/storage/covers/${album.coverId}.jpg`}/>
            }
            <Link to={`/albums/${album.id}`}>{album.name}</Link>
        </div>
    )
}

class AlbumsList extends React.Component{
    render(){
        return (
            <div className="albums-box">
                {this.props.albums.map(this.renderAlbum)}
            </div>
        )
    }
}
AlbumsList.propTypes = {
    albums: React.PropTypes.arrayOf(React.PropTypes.object),
};

export default AlbumsList;