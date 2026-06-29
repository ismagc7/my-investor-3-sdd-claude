import { httpClient } from '../../shared/api/httpClient';
import type { Launch, LaunchRequest } from '../../shared/types/launch';

export async function getLaunches(): Promise<Launch[]> {
  return httpClient.get<Launch[]>('/api/launches');
}

export async function getLaunchById(id: number): Promise<Launch> {
  return httpClient.get<Launch>(`/api/launches/${id}`);
}

export async function createLaunch(request: LaunchRequest): Promise<Launch> {
  return httpClient.post<Launch>('/api/launches', request);
}

export async function updateLaunch(id: number, request: LaunchRequest): Promise<Launch> {
  return httpClient.put<Launch>(`/api/launches/${id}`, request);
}

export async function deleteLaunch(id: number): Promise<void> {
  return httpClient.del(`/api/launches/${id}`);
}
