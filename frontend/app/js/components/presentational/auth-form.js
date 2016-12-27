import React from 'react'

class AuthForm extends React.Component {
    constructor(){
        super();
        this.changeLogin = this.changeLogin.bind(this);
        this.changePassword = this.changePassword.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleSignIn =this.handleSignIn.bind(this);
        this.handleSignUp =this.handleSignUp.bind(this);
        this.state = {
            login:"",password: ""
        }
    }

    handleSubmit(e){
        e.preventDefault();
    }

    changeLogin(e){
        this.setState({
            ...this.state,
            login: e.target.value
        });
    }

    changePassword(e){
        this.setState({
            ...this.state,
            password: e.target.value
        });
    }

    handleSignIn(e){
        this.props.onSignIn(this.state)
    }

    handleSignUp(e){
        this.props.onSignUp(this.state)
    }

    render() {
        return (
            <div className="row">
                <form onSubmit={this.handleSubmit } ref={(form)=>{this.form = form;}} className="col s12">
                    <div className="row">
                        <h4>You shall sign in or register before using this site</h4>
                    </div>
                    <div className="row">
                        <div className="input-field col s6">
                            <input onChange={this.changeLogin} name="login" id="login" type="text" className="validate" />
                            <label htmlFor="login">Username</label>
                        </div>
                        <div className="input-field col s6">
                            <input onChange={this.changePassword} name="password" id="password" type="password" className="validate" />
                            <label htmlFor="password">Password</label>
                        </div>
                    </div>
                    <div className="row">
                        <div className="col">
                        <button
                            onClick={this.handleSignIn}
                            className="colored waves-effect btn">
                            <i style={{"outline" : "none","verticalAlign":"middle"}} className="material-icons">perm_identity</i>
                            Sign In
                        </button>
                        </div>
                        <div className="col">
                        <button
                            onClick={this.handleSignUp}
                            className="colored waves-effect btn">
                            <i style={{"outline" : "none","verticalAlign":"middle"}} className="material-icons">person_add</i>
                            Register
                        </button>
                        </div>
                    </div>
                </form>
            </div>
        )
    }
}

export default AuthForm;