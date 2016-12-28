import React from 'react'
import {Link} from 'react-router'

class AlbumsView extends React.Component{
    constructor(){
        super();
        this.listAlbums = this.listAlbums.bind(this);
        this.showAlbum = this.showAlbum.bind(this);
        this.loadData = this.loadData.bind(this);

        this.state = {
            albums: []
        }
    }

    loadData(props){
        $.ajax({
            url: `/api/collection/${this.props.params.userId}/albums`
        }).done(data=>{
            this.setState({albums: data})
        })
    }

    componentWillMount(){
        this.loadData(this.props)
    }

    componentWillReceiveProps(newProps){
        if(newProps.params.userId !== this.props.params.userId){
            this.loadData(newProps)
        }
    }


    showAlbum(album){
        return(
            <div className="albums-box-element" key={album.id}>
                { album.coverId != null &&
                    <img src={`/storage/covers/${album.coverId}.jpg`}/>
                }
                <Link to={`/albums/${album.id}`}>{album.name}</Link>
            </div>
        )
    }

    listAlbums(){
        return this.state.albums.map(this.showAlbum);
    }

    render(){
        return (
            <div className="albums-box">
                {this.listAlbums()}
                {this.showAlbum({name: 'Unknown album', id: -this.props.params.userId})}
            </div>
        )
    }
}

export default AlbumsView