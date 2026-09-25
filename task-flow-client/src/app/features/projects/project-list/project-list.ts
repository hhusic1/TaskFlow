import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectService } from '../../../core/services/project';
import { Project } from '../../../core/models/project.model';

@Component({
  selector: 'app-project-list',
  imports: [CommonModule],
  templateUrl: './project-list.html',
  styleUrl: './project-list.scss'
})
export class ProjectList implements OnInit {
  projects = signal<Project[]>([]);

  constructor(private projectService: ProjectService) {}

ngOnInit(): void {
  console.log('KORAK 1: ngOnInit pozvan');
  this.projectService.getAll().subscribe({
    next: (data) => {
      console.log('KORAK 2: Podaci stigli:', data);
      this.projects.set(data);
      console.log('KORAK 3: Signal postavljen, trenutna vrijednost:', this.projects());
    },
    error: (err) => {
      console.error('KORAK 4: GREŠKA:', err);
    }
  });
}

}