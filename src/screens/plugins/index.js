import React, {Component} from 'react';
import { Container, Header, Content, Card, CardItem, Thumbnail, Text, Button, Icon, Left, Body, Right, Fab, Title, Badge, scaleX, scaleY} from 'native-base';

import { AppState, StyleSheet,  View, TouchableOpacity, Image, Alert, AppRegistry, NativeModules } from 'react-native';
import * as Animatable from 'react-native-animatable';
import Collapsible from 'react-native-collapsible';
import Accordion from 'react-native-collapsible/Accordion';


  const styles = StyleSheet.create({
    container: {
      flex: 1,
      justifyContent: 'center',
      backgroundColor: '#F5FCFF'
    },
    title: {
      textAlign: 'center',
      fontSize: 22,
      fontWeight: '300',
      marginBottom: 20
    },
    header: {
      backgroundColor: '#F5FCFF',
      padding: 10
    },
    headerText: {
      textAlign: 'left',
      fontSize: 16,
      fontWeight: '500'
    },
    content: {
      padding: 20,
      backgroundColor: '#fff'
    },
    active: {
      backgroundColor: 'rgba(255,255,255,1)'
    },
    inactive: {
      backgroundColor: 'rgba(245,252,255,1)'
    },
    selectors: {
      marginBottom: 10,
      flexDirection: 'row',
      justifyContent: 'center'
    },
    selector: {
      backgroundColor: '#F5FCFF',
      padding: 10
    },
    activeSelector: {
      fontWeight: 'bold'
    },
    selectTitle: {
      fontSize: 14,
      fontWeight: '500',
      padding: 10
    },
    card_button_text: {
      padding: 0,
      fontSize: 10,
    },
  });

class Plugins extends React.Component {

  state = {
      activeSection: false,
      collapsed: true,
      active: false,
      uniqueValue: 1,
      content:[],
    };
    componentWillUnmount() {
      console.log("----------------------unmount----------------");
      }
    _toggleExpanded = () => {
      this.setState({ collapsed: !this.state.collapsed });
    };

    _setSection(section) {
      this.setState({ activeSection: section });
    }
    _renderHeader=(section, i, isActive) => {
      return (
        <Animatable.View
          duration={400}
          style={{backgroundColor: '#131325'}}
          transition="backgroundColor"
        >
        <Card style={{backgroundColor: '#131325'}}>
                    <CardItem bordered style={{backgroundColor: '#1e2c3c'}}>
                      <Left>
                        <Body>
                          <Text style={{color: 'white'}}>{section.title}</Text>
                          <Text note>{section.summary}</Text>
                        </Body>
                      </Left>
                    </CardItem>
                    <CardItem boardered style={{backgroundColor: '#1e2c3c'}}>
                                  <Left>
                                    <Button transparent small warning disabled={this.addButtonStatus(section.packageName, section.isInstalled)}  onPress={() => NativeModules.ActivityStarter.pluginInstall(section.packageName)}>
                                    <Icon active name="ios-add-circle-outline" type="Ionicons"/>
                                      <Text style={styles.card_button_text}>Add</Text>
                                    </Button>
                                  </Left>
                                  <Body>
                                    <Button transparent small warning disabled={this.removeButtonStatus(section.packageName, section.isInstalled)} onPress={() => NativeModules.ActivityStarter.pluginUnInstall(section.packageName)}>
                                      <Icon active name="ios-remove-circle-outline" type="Ionicons"/>
                                      <Text style={styles.card_button_text}>Remove</Text>
                                    </Button>
                                  </Body>
                                  <Right>
                                  <Button transparent small warning={section.isConfigured} danger={this.isNotConfigured(section.isConfigured)} disabled={this.settingsButtonStatus(section.packageName, section.isInstalled)} onPress={() => NativeModules.ActivityStarter.pluginSettings(section.packageName)}>
                                    <Icon active name="ios-settings-outline" type="Ionicons"/>

                                    <Text style={styles.card_button_text}>Settings</Text>
                                  </Button>
                                  </Right>
                                </CardItem>
                                                  </Card>
                          </Animatable.View>

      );
    }


    addButtonStatus=(packageName, isInstalled)=>{
      if (packageName=='org.md2k.mcerebrum')
        return true;
        else return isInstalled;
    }
    removeButtonStatus=(packageName, isInstalled)=>{
      if (packageName=='org.md2k.mcerebrum')
        return true;

        else return !isInstalled;
    }
    settingsButtonStatus=(packageName, isInstalled)=>{
      if (packageName=='org.md2k.mcerebrum')
        return false;

        else return !isInstalled;
    }
    isSettings=()=>{
      if(this.state.content && this.state.content.length>0){
        for (var i=0; i<this.state.content.length; i++) {
            if(this.state.content[i].isConfigured==true){
              this.props.navigator.setTabBadge({
                badgeColor: 'red', // (optional) if missing, the badge will use the default color
              });
              
            return true;
          }
        }
        this.props.navigator.setTabBadge({
          badge: 1, // badge value, null to remove badge
          badgeColor: 'red', // (optional) if missing, the badge will use the default color
        });
        return false;
      }else {
        this.props.navigator.setTabBadge({
          badgeColor: 'red', // (optional) if missing, the badge will use the default color
        });

        return true;
    }
  }
    isNotConfigured=(isConfigured)=>{
      if (isConfigured==true)
        return false;

        else return true;
    }


    _renderContent(section, i, isActive) {
      return (
        <Animatable.View
          duration={0}
          style={[styles.content, isActive ? styles.active : styles.inactive]}
          transition="backgroundColor"
        >
        </Animatable.View>
      );
    }
    constructor(props){
      super(props);
      this.props.navigator.setTitle({title: 'mCerebrum : Plugins'});
      this.props.navigator.addOnNavigatorEvent(this.onNavigatorEvent.bind(this));
      {this.read()};

    }
    onNavigatorEvent(event) {
      console.log('plugin ..event='+event.id);
      switch(event.id) {
        case 'didAppear':
        case 'onActivityResumed':
        {this.read()};
        break;

      }
    }

    read = () =>{
      console.log('reading plugins.....');
      NativeModules.ActivityStarter.getPackageList(result=>{
        this.state.content = JSON.parse(result);
        console.log('plugin...after read...');
        {this.isSettings()}
        {this.forceRemount()};

    });
  }
    forceRemount = () => {
      this.setState({
        uniqueValue: this.state.uniqueValue + 1
      });
    }

    render() {
      return (
        <Container style={{backgroundColor: '#131325'}} key={this.state.uniqueValue}>
                <Content>
 <Accordion
            activeSection={this.state.activeSection}
            sections={this.state.content}
            touchableComponent={TouchableOpacity}
            renderHeader={this._renderHeader}
            renderContent={this._renderContent}
            duration={400}
            onChange={this._setSection.bind(this)}
          />
          </Content>
                </Container>                );
    }
  }

export default Plugins;
