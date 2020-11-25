package com.thehuxley.event

import com.thehuxley.Problem
import com.thehuxley.Submission


class SubmissionSentMetric implements Listener, Metric {


	def final name = "PROBLEM_SUBMISSION_SENT"

	def static instance

	def stats = [:]

	private SubmissionSentMetric() {
		EventManager.getInstance().addListener(this, EventManager.Event.SUBMISSION_NEW)
		EventManager.getInstance().addListener(this, EventManager.Event.SUBMISSION_REEVALUATE)
	}


	synchronized static SubmissionSentMetric getInstance() {
		if (!instance) {
			instance = new SubmissionSentMetric()
		}

		return instance
	}

	@Override
	String getName() {
		return name
	}

	@Override
	void receiveUpdate(EventManager.Event event, Map params) {

		if (params.submission) {

			Submission submission = params.submission as Submission

			if (event == EventManager.Event.SUBMISSION_REEVALUATE) {
				stats[submission.problem?.id] = null
			}

			if (!stats[submission.problem?.id]) {
				build(submission.problem)
			} else {
				update(submission.problem)
			}

		}

	}

	def build(Problem problem) {
		stats[problem.id] = Submission.countByProblem(problem)
	}

	def update(Problem problem) {
		stats[problem.id]++
	}

	Map getMetric(Map params) {

		if (params.problem) {
			Problem problem = params.problem as Problem
			return [name : stats[problem.id]]
		}

		return null
	}

}
