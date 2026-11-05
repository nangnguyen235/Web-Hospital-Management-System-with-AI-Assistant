using MediatR;

namespace HIS.SharedKernel.Events;

/// <summary>Sự kiện giữa các module (4 sự kiện ở modular-monolith.md mục 3.1), phát SAU commit.</summary>
public interface IIntegrationEvent : INotification;

public interface IEventBus
{
    Task PublishAsync<TEvent>(TEvent integrationEvent, CancellationToken cancellationToken = default)
        where TEvent : IIntegrationEvent;
}
