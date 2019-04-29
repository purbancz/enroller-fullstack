package com.company.enroller.controllers;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/api/participants")
public class ParticipantRestController {

    @Autowired
    ParticipantService participantService;
    
    @Autowired
    MeetingService meetingService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAll() {
        Collection<Participant> participants = participantService.getAll();
        return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable("id") String login) {
        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Participant>(participant, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipant(@RequestBody Participant participant) {
        if (participantService.findByLogin(participant.getLogin()) != null) {
            return new ResponseEntity(
                    "Unable to create. A participant with login " + participant.getLogin() + " already exist.",
                    HttpStatus.CONFLICT);
        }
        participantService.add(participant);
        return new ResponseEntity<Participant>(participant, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") String login) {
        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        participantService.delete(participant);
        return new ResponseEntity<Participant>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@PathVariable("id") String login,
                                    @RequestBody Participant updatedParticipant) {
        if (participantService.findByLogin(login) != null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        updatedParticipant.setLogin(login); // in case of login!=updatedParticipant.getLogin()
        participantService.update(updatedParticipant);
        return new ResponseEntity<Participant>(HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/{id}/meetings/", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetingsByParticipant(@PathVariable("login") String string) {
		Participant participant = participantService.findByLogin(string);
		if (participant == null) {
			return new ResponseEntity<Participant>(HttpStatus.NOT_FOUND);
		} else {
			Collection<Meeting> meetings = meetingService.getAll();
			Collection<Meeting> searchQuery = new ArrayList<Meeting>();
			for (Meeting meeting : meetings) {
				if (meeting.getParticipants().contains(participant)) {
					searchQuery.add(meeting);
				}
			}
			if (searchQuery.isEmpty()) {
				return new ResponseEntity("Participant has not been enrolled to any meeting yet.", HttpStatus.OK);
			} else {
				return new ResponseEntity<Collection<Meeting>>(searchQuery, HttpStatus.OK);
			}
		}
	}
}
