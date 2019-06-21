import { Component, OnInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { AuthenticationService } from '../_services/authentication.service';

@Component({
  selector: 'app-faq',
  templateUrl: './faq.component.html',
  styleUrls: ['./faq.component.scss']
})
export class FaqComponent implements OnInit {
  questionsAndAnswers: { question: string, answer: string }[];
  supportEmail: string;

  bossQA: { question: string, answer: string }[] = [
    {
      question: 'Was passiert mit den Projektanfragen meiner Mitarbeiter, wenn sie keinen Zugang mehr haben?',
      answer: 'Die Anfragen werden einen Monat nachdem der Zugang deaktiviert wurde gelöscht.'
    },
    {
      question: 'Wer wird über die Anfragen meiner Mitarbeiter benachrichtigt?',
      answer: 'Nur Du wirst über die Projektanfragen Deiner direkten Mitarbeiter per Email benachrichtigt. ' +
        'Dein Vorgesetzter kann die Anfragen jedoch ebenfalls im project board einsehen.'
    },
    {
      question: 'Was passiert beim Löschen der Projektanfragen meiner Mitarbeiter?',
      answer: 'Die Anfrage wird komplett gelöscht. Das Projekt kann erneut angefragt werden. ' //ToDo: Kommentar im Jira wird nicht entfernt
    },
    {
      question: 'Haben meine Mitarbeiter ein anderes FAQ?',
      answer: 'Ja, Deine Mitarbeiter sehen auf dieser Seite andere Fragen und Antworten.'
    },
    {
      question: 'Haben meine Mitarbeiter die gleichen Informationen über die Projekte wie ich?',
      answer: 'Nein. Sie können die Felder "Externe", "Reisekosten vergütet" und "Tagessatz" nicht sehen.'
    },
    {
      question: 'Sind das alles aktuelle Projekte bei adesso?',
      answer: 'Aufgelistet sind alle Projekte aus dem Jira. ' +
        'Es handelt sich um Akquisen, um Mitarbeiter für ein Projekt zu finden. ' +
        'Die Projekte befinden sich eventuell noch in der Planung.'
    }
  ];

  employeeQA: { question: string, answer: string }[] = [
    {
      question: 'Was passiert, wenn ich eine Projektanfrage absende?',
      answer: 'Dein Vorgesetzter bekommt eine Benachrichtigung über Dein Interesse an diesem Projekt.'
    },
    {
      question: 'Was hat es mit den Lesezeichen auf sich?',
      answer: 'Du kannst Projekte, an denen Du Interesse hast, mit einem Lesezeichen markieren. ' +
        'Deine Lesezeichen kannst nur Du sehen.'
    },
    {
      question: 'Sind das alles aktuelle Projekte bei adesso?',
      answer: 'Aufgelistet sind alle Projekte aus dem Jira. ' +
        'Es handelt sich um Akquisen, um Mitarbeiter für ein Projekt zu finden. ' +
        'Die Projekte befinden sich eventuell noch in der Planung.'
    },
    {
      question: 'Wie lange kann ich das project board nutzen?',
      answer: 'Die Dauer der Freischaltung kann Dein Vorgesetzter festlegen. ' +
        'Dieser kann Deine Freischaltung jederzeit verlängern oder deaktivieren.'
    }
  ];

  constructor(private authService: AuthenticationService) {}

  ngOnInit(): void {
    this.questionsAndAnswers = this.authService.isBoss ? this.bossQA : this.employeeQA;
    this.supportEmail = environment.supportEmail;
  }
}
