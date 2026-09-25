export interface Project {
  id: number;
  name: string;
  description: string | null;
  createdAt: string;
}

export interface CreateProject {
  name: string;
  description: string | null;
}