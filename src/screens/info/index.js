import React, {Component} from 'react';
import { Container, Header, Content, Card, CardItem, Thumbnail, Text, Button, Icon, Left, Body, Right, Fab, Title, List, ListItem } from 'native-base';

import { YellowBox, StyleSheet,  View, TouchableOpacity, Image, Alert, AppRegistry, NativeModules } from 'react-native';
import * as Animatable from 'react-native-animatable';
import Collapsible from 'react-native-collapsible';
import Accordion from 'react-native-collapsible/Accordion';
YellowBox.ignoreWarnings(['Warning: isMounted(...) is deprecated', 'Module RCTImageLoader']);


const MCEREBRUM = [
  {
    title: 'Phone',
    list: [
      "Accelerometer",
      "Activity Type",
      "Ambient Light",
      "Ambient Temperature",
      "Battery",
      "Compass",
      "CPU",
      "Geofence",
      "Gyroscope",
      "Location",
      "Memory",
      "Pressure",
      "Proximity",
      "Step Count",
      "Touch Screen",
      "App Usage",
      "Notification",
      "SMS",
      "Call Receord"
    ]
  },
  {
    title: 'MotionSenseHRV',
    list:[
      "Accelerometer",
      "Gyroscope",
      "LED",
      "Battery",
      "Sequence Number",
      "Data Quality (Accelerometer)",
      "Data Quality (LED)",
      "Raw Sample",
    ]
  },
  {
    title: 'MotionSenseHRV+',
    list:[
      "Accelerometer",
      "Quaternion",
      "LED",
      "Magnetometer",
      "Magnetometer Sensitivity",
      "Battery",
      "Sequence Number",
      "Data Quality (Accelerometer)",
      "Raw Sample",
    ]
  }
];
const CEREBRAL_CORTEX = [
  {
    title: "Workplace Behavior",
    list:[
      "org.md2k.data_analysis.feature.cumulative_staying_time.entertainment",
      "org.md2k.data_analysis.feature.cumulative_staying_time.home",
      "org.md2k.data_analysis.feature.cumulative_staying_time.work",
      "org.md2k.data_analysis.feature.cumulative_staying_time.worship",
      "org.md2k.data_analysis.feature.cumulative_staying_time.school",
      "org.md2k.data_analysis.feature.cumulative_staying_time.store",
      "org.md2k.data_analysis.feature.cumulative_staying_time.sport",
      "org.md2k.data_analysis.feature.cumulative_staying_time.restbar",
      "org.md2k.data_analysis.feature.task_features.activity_office_context.daily&found_activity",
      "org.md2k.data_analysis.feature.task_features.activity_office_context.daily&context_type",
      "org.md2k.data_analysis.feature.task_features.activity_office_context_fraction.per_hour&activity_type",
      "org.md2k.data_analysis.feature.task_features.activity_office_context_fraction.per_hour&activity_time"
              ]
  },
  {
    title: "Mobility Behavior",
    list:[
      "org.md2k.data_analysis.feature.expected_conservative_office_arrival_times",
      "org.md2k.data_analysis.feature.expected_conservative_office_staying_times",
      "org.md2k.data_analysis.feature.expected_liberal_office_arrival_times",
      "org.md2k.data_analysis.feature.expected_liberal_office_staying_times",
      "org.md2k.data_analysis.feature.office_arrival_times",
      "org.md2k.data_analysis.feature.office_staying_times",
      "org.md2k.data_analysis.feature.total_distance_covered",
      "org.md2k.data_analysis.feature.to_home_transitions",
      "org.md2k.data_analysis.feature.to_work_transitions",
      "org.md2k.data_analysis.feature.transition_counter"
          ]
  },
  {
    title: "Location",
    list:[
      "org.md2k.data_analysis.gps_clustering_episode_generation&index",
      "org.md2k.data_analysis.gps_clustering_episode_generation&centroid_latitude",
      "org.md2k.data_analysis.gps_clustering_episode_generation&centroid_longitude",
      "org.md2k.data_analysis.gps_clustering_episode_generation_daily&index",
      "org.md2k.data_analysis.gps_clustering_episode_generation_daily&centroid_latitude",
      "org.md2k.data_analysis.gps_clustering_episode_generation_daily&centroid_longitude",
      "org.md2k.data_analysis.gps_data_centroid_index",
      "org.md2k.data_analysis.gps_episodes_and_semantic_location",
      "org.md2k.data_analysis.gps_episodes_and_semantic_location_daily",
      "org.md2k.data_analysis.gps_episodes_and_semantic_location_from_model",
      "org.md2k.data_analysis.gps_episodes_and_semantic_location_from_places&restaurant_bar_nearby",
      "org.md2k.data_analysis.gps_episodes_and_semantic_location_from_places&school",
      "org.md2k.data_analysis.gps_episodes_and_semantic_location_from_places&place_of_worship_nearby",
      "org.md2k.data_analysis.gps_episodes_and_semantic_location_from_places&entertainment_nearby",
      "org.md2k.data_analysis.gps_episodes_and_semantic_location_from_places&store_nearby",
      "org.md2k.data_analysis.gps_episodes_and_semantic_location_from_places&sports_arena_nearby",
      "org.md2k.data_analysis.gps_episodes_and_semantic_location_user_marked",
      "org.md2k.data_analysis.gps_episodes_and_semantic_location_user_marked_daily"
              ]
  },

  {
    title: "Phone Call Information",
    list:[
      "org.md2k.data_analysis.feature.phone.call.day.count&value",
      "org.md2k.data_analysis.feature.phone.call.day.entropy&value",
      "org.md2k.data_analysis.feature.phone.call.four_hour.count&value",
      "org.md2k.data_analysis.feature.phone.call.four_hour.entropy&value",
      "org.md2k.data_analysis.feature.phone.call.hour.count&value",
      "org.md2k.data_analysis.feature.phone.call.hour.entropy&value",
      "org.md2k.data_analysis.feature.phone.call_duration.day.average&value",
      "org.md2k.data_analysis.feature.phone.call_duration.day.variance&value",
      "org.md2k.data_analysis.feature.phone.call_duration.four_hour.average&value",
      "org.md2k.data_analysis.feature.phone.call_duration.four_hour.variance&value",
      "org.md2k.data_analysis.feature.phone.call_duration.hour.average&value",
      "org.md2k.data_analysis.feature.phone.call_duration.hour.variance&value",
      "org.md2k.data_analysis.feature.phone.inter_event_phone_call_sms_time.day.average&value",
      "org.md2k.data_analysis.feature.phone.inter_event_phone_call_sms_time.day.variance&value",
      "org.md2k.data_analysis.feature.phone.inter_event_phone_call_sms_time.four_hour.average&value",
      "org.md2k.data_analysis.feature.phone.inter_event_phone_call_sms_time.four_hour.variance&value",
      "org.md2k.data_analysis.feature.phone.inter_event_phone_call_time.day.average&value",
      "org.md2k.data_analysis.feature.phone.inter_event_phone_call_time.day.variance&value",
      "org.md2k.data_analysis.feature.phone.inter_event_phone_call_time.four_hour.average&value",
      "org.md2k.data_analysis.feature.phone.inter_event_phone_call_time.four_hour.variance&value",
      "org.md2k.data_analysis.feature.phone.inter_event_phone_call_time.hour.average&value",
      "org.md2k.data_analysis.feature.phone.inter_event_phone_call_time.hour.variance&value",
      "org.md2k.data_analysis.v1.feature.phone.call.day.initiated&percent",
      ]
  },
  {
    title: "Messaging",
    list:[
      "org.md2k.data_analysis.feature.phone.call_sms.day.count&value",
      "org.md2k.data_analysis.feature.phone.call_sms.day.entropy&value",
      "org.md2k.data_analysis.feature.phone.call_sms.four_hour.count&value",
      "org.md2k.data_analysis.feature.phone.call_sms.four_hour.entropy&value",
      "org.md2k.data_analysis.feature.phone.call_sms.hour.count&value",
      "org.md2k.data_analysis.feature.phone.call_sms.hour.entropy&value",
      "org.md2k.data_analysis.feature.phone.inter_event_phone_call_sms_time.hour.average&value",
      "org.md2k.data_analysis.feature.phone.inter_event_phone_call_sms_time.hour.variance&value",
      "org.md2k.data_analysis.feature.phone.inter_event_sms_time.day.average&value",
      "org.md2k.data_analysis.feature.phone.inter_event_sms_time.day.variance&value",
      "org.md2k.data_analysis.feature.phone.inter_event_sms_time.four_hour.average&value",
      "org.md2k.data_analysis.feature.phone.inter_event_sms_time.four_hour.variance&value",
      "org.md2k.data_analysis.feature.phone.inter_event_sms_time.hour.average&value",
      "org.md2k.data_analysis.feature.phone.inter_event_sms_time.hour.variance&value",
      "org.md2k.data_analysis.feature.phone.sms.day.entropy&value",
      "org.md2k.data_analysis.feature.phone.sms.four_hour.count&value",
      "org.md2k.data_analysis.feature.phone.sms.four_hour.entropy&value",
      "org.md2k.data_analysis.feature.phone.sms.hour.count&value",
      "org.md2k.data_analysis.feature.phone.sms.hour.entropy&value",
      "org.md2k.data_analysis.feature.phone.sms_length.day.average&value",
      "org.md2k.data_analysis.feature.phone.sms_length.day.variance&value",
      "org.md2k.data_analysis.feature.phone.sms_length.four_hour.average&value",
      "org.md2k.data_analysis.feature.phone.sms_length.four_hour.variance&value",
      "org.md2k.data_analysis.feature.phone.sms_length.hour.average&value",
      "org.md2k.data_analysis.feature.phone.sms_length.hour.variance&value",
      "org.md2k.data_analysis.v1.feature.phone.sms.day.initiated&percent",
      "org.md2k.data_analysis.feature.phone.callsms.day.initiated&percent "
    ]
  },
  {
    title: "App Usage",
    list:[
      "org.md2k.data_analysis.feature.phone.app_usage.all_context.total&duration",
      "org.md2k.data_analysis.feature.phone.app_usage.Books_Reference.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Books_Reference.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Books_Reference.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Books_Reference.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Books_Reference.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Books_Reference.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Books_Reference.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Books_Reference.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Communication.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Communication.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Communication.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Communication.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Communication.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Communication.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Communication.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Communication.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Education.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Education.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Education.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Education.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Education.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Education.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Education.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Education.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Entertainment.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Entertainment.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Entertainment.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Entertainment.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Entertainment.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Entertainment.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Entertainment.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Entertainment.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Finance.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Finance.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Finance.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Finance.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Finance.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Finance.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Finance.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Finance.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Food_Drink.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Food_Drink.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Food_Drink.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Food_Drink.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Food_Drink.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Food_Drink.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Food_Drink.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Food_Drink.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Game.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Game.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Game.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Game.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Game.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Game.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Game.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Game.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Health_Fitness.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Health_Fitness.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Health_Fitness.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Health_Fitness.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Health_Fitness.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Health_Fitness.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Health_Fitness.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Health_Fitness.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Lifestyle.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Lifestyle.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Lifestyle.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Lifestyle.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Lifestyle.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Lifestyle.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Lifestyle.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Lifestyle.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Maps_Navigation.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Maps_Navigation.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Maps_Navigation.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Maps_Navigation.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Maps_Navigation.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Maps_Navigation.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Maps_Navigation.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Maps_Navigation.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Music_Audio.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Music_Audio.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Music_Audio.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Music_Audio.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Music_Audio.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Music_Audio.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Music_Audio.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Music_Audio.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.News_Magazines.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.News_Magazines.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.News_Magazines.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.News_Magazines.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.News_Magazines.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.News_Magazines.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.News_Magazines.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.News_Magazines.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.outside.per_hour&value",
      "org.md2k.data_analysis.feature.phone.app_usage.outside.total&value",
      "org.md2k.data_analysis.feature.phone.app_usage.Personalization.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Personalization.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Personalization.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Personalization.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Personalization.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Personalization.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Personalization.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Personalization.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Photography.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Photography.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Photography.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Photography.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Photography.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Photography.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Photography.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Photography.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Productivity.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Productivity.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Productivity.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Productivity.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Productivity.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Productivity.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Productivity.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Productivity.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Shopping.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Shopping.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Shopping.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Shopping.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Shopping.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Shopping.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Shopping.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Shopping.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Social.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Social.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Social.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Social.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Social.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Social.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Social.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Social.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Sports.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Sports.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Sports.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Sports.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Sports.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Sports.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Sports.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Sports.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Tools.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Tools.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Tools.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Tools.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Tools.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Tools.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Tools.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Tools.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Travel_Local.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Travel_Local.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Travel_Local.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Travel_Local.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Travel_Local.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Travel_Local.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Travel_Local.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Travel_Local.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.VideoPlayers_Editors.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.VideoPlayers_Editors.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.VideoPlayers_Editors.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.VideoPlayers_Editors.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.VideoPlayers_Editors.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.VideoPlayers_Editors.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.VideoPlayers_Editors.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.VideoPlayers_Editors.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Weather.all_context.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Weather.all_context.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Weather.home.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Weather.home.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Weather.outside.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Weather.outside.total",
      "org.md2k.data_analysis.feature.phone.app_usage.Weather.work.per_hour",
      "org.md2k.data_analysis.feature.phone.app_usage.Weather.work.total",
      "org.md2k.data_analysis.feature.phone.app_usage.work.per_hour&duration",
      "org.md2k.data_analysis.feature.phone.app_usage.work.total&duration",
      "org.md2k.data_analysis.feature.phone.app_usage_category&package",
      "org.md2k.data_analysis.feature.phone.app_usage_category&category",
      "org.md2k.data_analysis.feature.phone.app_usage_category&app_name",
      "org.md2k.data_analysis.feature.phone.app_usage_category&sub_category",
      "org.md2k.data_analysis.feature.phone.app_usage_interval"
    ]
  },
  {
    title: "Ambient Light",
    list:[
      "org.md2k.data_analysis.feature.phone.ambient_light.day.average&value",
      "org.md2k.data_analysis.feature.phone.ambient_light.day.variance&value",
      "org.md2k.data_analysis.feature.phone.ambient_light.four_hour.average&value",
      "org.md2k.data_analysis.feature.phone.ambient_light.four_hour.variance&value",
      "org.md2k.data_analysis.feature.phone.ambient_light.hour.average&value",
      "org.md2k.data_analysis.feature.phone.ambient_light.hour.variance&value"
    ]
  },
  {
    title: "Posture",
    list:[
      "org.md2k.data_analysis.feature.body_posture.wrist.10_second",
      "org.md2k.data_analysis.feature.body_posture.wrist.accel_only.10_second",
      "org.md2k.data_analysis.feature.task_features.posture_office_context.daily&found_posture",
      "org.md2k.data_analysis.feature.task_features.posture_office_context.daily&context_type",
      "org.md2k.data_analysis.feature.task_features.posture_office_context_fraction.per_hour&posture_type",
      "org.md2k.data_analysis.feature.task_features.posture_office_context_fraction.per_hour&posture_time",
      "org.md2k.data_analysis.feature.task_features.sitting_office_context_totaltime_and_fraction_per_hour.daily&posture_time_total",
      "org.md2k.data_analysis.feature.task_features.sitting_office_context_totaltime_and_fraction_per_hour.daily&posture_time_fraction",
      "org.md2k.data_analysis.feature.task_features.standing_office_context_totaltime_and_fraction_per_hour.daily&posture_time_total",
      "org.md2k.data_analysis.feature.task_features.standing_office_context_totaltime_and_fraction_per_hour.daily&posture_time_fraction"
    ]
  },
  {
    title: "Activity Summary",
    list:[
      "org.md2k.data_analysis.feature.phone.driving_total.day",
      "org.md2k.data_analysis.feature.phone.bicycle_total.day",
      "org.md2k.data_analysis.feature.phone.still_total.day",
      "org.md2k.data_analysis.feature.phone.on_foot_total.day",
      "org.md2k.data_analysis.feature.phone.tilting_total.day",
      "org.md2k.data_analysis.feature.phone.walking_total.day",
      "org.md2k.data_analysis.feature.phone.running_total.day",
      "org.md2k.data_analysis.feature.phone.unknown_total.day"
    ]
  },
  {
    title: "Stress",
    list:[
      "org.md2k.data_analysis.feature.stress.wrist.60seconds.v1",
      "org.md2k.data_analysis.feature.stress.wrist.likelihood.60minutes.v1",
      "org.md2k.data_analysis.feature.stress.wrist.likelihood.60seconds.v1"
    ]
  },
  {
    title: "Smoking",
    list:[
      "org.md2k.data_analysis.feature.puffmarker.smoking_episode",
      "org.md2k.data_analysis.feature.puffmarker.smoking_puff"
    ]
  }
];

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
    backgroundColor: '#131325'
  },
  inactive: {
    backgroundColor: '#131325'
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

class Info extends React.Component {

  state = {
    activeSection: false,
    activeSectionCC: false,
    collapsed: true,
    active: false
  };

  _toggleExpanded = () => {
    this.setState({ collapsed: !this.state.collapsed });
  };

  _setSection(section) {
    this.setState({ activeSection: section });
  }
  _setSectionCC(section) {
    this.setState({ activeSectionCC: section });
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
      <Body>
      <Text style={{color: 'white'}}>{section.title}</Text>
      </Body>
      <Right>
      <Icon name="ios-arrow-down-outline"/>
      </Right>
      </CardItem>
      </Card>
      </Animatable.View>

    );
  }
  _renderHeaderCC=(section, i, isActive) => {
    return (
      <Animatable.View
      duration={400}
      style={{backgroundColor: '#131325'}}
      transition="backgroundColor"
      >
      <Card style={{backgroundColor: '#131325'}}>
      <CardItem bordered style={{backgroundColor: '#1e2c3c'}}>
      <Body>
      <Text style={{color: 'white'}}>{section.title}</Text>
      </Body>
      <Right>
      <Icon name="ios-arrow-down-outline"/>
      </Right>
      </CardItem>
      </Card>
      </Animatable.View>

    );
  }

  addButtonStatus=(packageName)=>{
    if (packageName=='org.md2k.mcerebrum')
    return true;
    else return false;
  }
  removeButtonStatus=(packageName)=>{
    if (packageName=='org.md2k.mcerebrum')
    return true;
    else return false;
  }

  _renderContent(section, i, isActive) {
    return (
      <Animatable.View
      duration={400}
      style={[styles.content, isActive ? styles.active : styles.inactive]}
      transition="backgroundColor"
      >
      <List dataArray={section.list}
      renderRow={(item) =>
        <ListItem>
        <Text note >{item}</Text>
        </ListItem>
      }>
      </List>
      </Animatable.View>
    );
  }
  _renderContentCC(section, i, isActive) {
    return (
      <Animatable.View
      duration={400}
      style={[styles.content, isActive ? styles.active : styles.inactive]}
      transition="backgroundColor"
      >
      <List dataArray={section.list}
      renderRow={(item) =>
        <ListItem>
        <Text note >{item}</Text>
        </ListItem>
      }>
      </List>
      </Animatable.View>
    );
  }

  constructor(props){
    super(props);
    this.props.navigator.setTitle({title: 'Supported Markers'});
  }

  render() {
    return (
      <Container style={{backgroundColor: '#131325'}}>
      <Content>

      <Text  style={{ backgroundColor: '#00838F', color: 'white', fontSize: 20, textAlign: 'center', lineHeight: 50}}>In mCerebrum</Text>
      <Accordion
      activeSection={this.state.activeSection}
      sections={MCEREBRUM}
      touchableComponent={TouchableOpacity}
      renderHeader={this._renderHeader}
      renderContent={this._renderContent}
      duration={400}
      onChange={this._setSection.bind(this)}
      />
      <Text  style={{ backgroundColor: '#00838F', color: 'white', fontSize: 20, textAlign: 'center', lineHeight: 50}}>In Cerebral Cortex</Text>
      <Accordion
      activeSectionCC={this.state.activeSectionCC}
      sections={CEREBRAL_CORTEX}
      touchableComponent={TouchableOpacity}
      renderHeader={this._renderHeaderCC}
      renderContent={this._renderContentCC}
      duration={400}
      onChange={this._setSectionCC.bind(this)}
      />

      </Content>
      </Container>
    );
    }
  }

  export default Info;
