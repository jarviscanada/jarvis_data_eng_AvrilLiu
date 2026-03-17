# Cloud, K8S, and DevOps

This project demonstrates a cloud-native deployment workflow using Docker, Kubernetes (AKS), Azure Container Registry (ACR), and Jenkins CI/CD pipelines.  
It showcases how a containerized Spring Boot application can be built, pushed, and deployed to a Kubernetes cluster with automated pipelines.

---

## Architecture

<p align="center">
  <img src="assets/Azure Scale Set.png" width="500">
</p>

The system is deployed on Azure Kubernetes Service (AKS) and includes:

- A LoadBalancer service to expose the application externally
- Multiple Spring Boot pods for scalability and high availability
- A PostgreSQL database pod for persistent storage
- Azure VM Scale Set nodes to run container workloads

---

## CI/CD Pipeline

The deployment process is fully automated using Jenkins:

1. Developer pushes code to GitHub
2. Jenkins pipeline is triggered
3. Docker image is built using the application source
4. Image is pushed to Azure Container Registry (ACR)
5. Kubernetes deployment is updated using kubectl
6. Rolling update ensures zero downtime deployment

---

## Tech Stack

- Java, Spring Boot
- Docker
- Kubernetes (AKS)
- Azure Container Registry (ACR)
- Jenkins (CI/CD)
- PostgreSQL

---

## Key Features

- Containerized microservice deployment
- Automated CI/CD pipeline with Jenkins
- Kubernetes-based orchestration and scaling
- Secure configuration using Kubernetes Secrets
- Persistent storage with volumes

---

## Project Structure

    cloud_k8_jenkins
    ├── aks
    ├── minikube
    ├── springboot
    │   ├── Jenkinsfile-dev
    │   └── Jenkinsfile-prod
    ├── assets
    └── README.md

---

## Summary

This project demonstrates practical experience in building and deploying cloud-native applications using modern DevOps practices, including containerization, orchestration, and CI/CD automation.