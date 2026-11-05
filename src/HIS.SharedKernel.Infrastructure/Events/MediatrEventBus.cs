using HIS.SharedKernel.Events;
using MediatR;

namespace HIS.SharedKernel.Infrastructure.Events;

/// <summary>Event bus trong bộ nhớ: chuyển IntegrationEvent cho các handler (INotificationHandler) của module nhận.</summary>
public sealed class MediatrEventBus(IPublisher publisher) : IEventBus
{
    public Task PublishAsync<TEvent>(TEvent integrationEvent, CancellationToken cancellationToken = default)
        where TEvent : IIntegrationEvent =>
        publisher.Publish(integrationEvent, cancellationToken);
}
