<template>
  <div>
    <new-meeting-form @added="addNewMeeting($event)"></new-meeting-form>

    <span v-if="meetings.length == 0">
               Brak zaplanowanych spotkań.
           </span>
    <h3 v-else>
      Zaplanowane zajęcia ({{ meetings.length }})
    </h3>

    <meetings-list :meetings="meetings"
                   :username="username"
                   @attend="addMeetingParticipant($event)"
                   @unattend="removeMeetingParticipant($event)"
                   @delete="deleteMeeting($event)"></meetings-list>
  </div>
</template>

<script>
    import NewMeetingForm from "./NewMeetingForm";
    import MeetingsList from "./MeetingsList";

    export default {
        components: {NewMeetingForm, MeetingsList},
        props: ['username'],
        data() {
            return {
                meetings: []
            };
        },
        methods: {
            addNewMeeting(meeting) {
                this.$http.post('meetings', meeting);
                    this.meetings.push(meeting);
            },
            addMeetingParticipant(meeting) {
                this.$http.post(`meetings/${meeting.id}/participants`)
                    .then(response => meeting.participants.push(response.body));
            },
            removeMeetingParticipant(meeting) {
                meeting.participants.splice(meeting.participants.indexOf(this.username), 1);
            },
            deleteMeeting(meeting) {
                this.$http.delete('meetings/${meeting.id}', meeting);
                this.meetings.splice(this.meetings.indexOf(meeting), 1);
            },
            getMeetings() {
                this.$http.get('meetings')
                    .then(response => {this.meetings = response.body;
                    }, response => {console.log('Błąd. Kod odpowiedzi: ' + response.status)});
            },
            getMeetingsParticipants() {
                for (meeting in this.meetings) {
                    this.$http.get('meetings/'+meeting.id+'/participants')
                        then(response => {meeting.participants = response.body;
                    }, response => {console.log('dupa')});
                    console.log(meeting.participants)
                }
            },  
        },
        mounted() {
            this.getMeetings();
            // this.getMeetingsParticipants();
        },
    }
</script>
