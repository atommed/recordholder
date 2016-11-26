import React from 'react'

class AuthForm extends React.Component {
    handleSubmit(e) {
        e.preventDefault();
        this.props.onSignIn(new FormData(this.form))
    }

    constructor(){
        super();
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    render() {
        return (
            <div className="row">
                <form ref={(form)=>{this.form = form;}} onSubmit={this.handleSubmit} className="col s12">
                    <div className="row">
                        <h4>You shall sign in or register before using this site</h4>
                    </div>
                    <div className="row">
                        <div className="input-field col s6">
                            <input name="login" id="login" type="text" className="validate" />
                            <label htmlFor="login">Username</label>
                        </div>
                        <div className="input-field col s6">
                            <input name="password" id="password" type="password" className="validate" />
                            <label htmlFor="password">Password</label>
                        </div>
                    </div>
                    <div className="row">
                        <button
                            className="colored waves-effect btn">
                            <i style={{"outline" : "none","verticalAlign":"middle"}} className="material-icons">perm_identity</i>
                            Sign In/Register
                        </button>
                    </div>
                </form>
            </div>
        )
    }
}

export default AuthForm;